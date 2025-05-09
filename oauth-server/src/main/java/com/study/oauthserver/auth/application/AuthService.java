package com.study.oauthserver.auth.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.oauthserver.global.auth.TokenProvider;
import com.study.oauthserver.global.auth.dto.TokenDto;
import com.study.oauthserver.auth.api.dto.request.LoginReqDto;
import com.study.oauthserver.auth.api.dto.response.LoginResDto;
import com.study.oauthserver.member.domain.Member;
import com.study.oauthserver.member.domain.Role;
import com.study.oauthserver.member.domain.SocialType;
import com.study.oauthserver.member.domain.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	// 카카오
	private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
	private static final String KAKAO_PROFILE_URI = "https://kapi.kakao.com/v2/user/me";

	// 구글
	private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
	private static final String GOOGLE_PROFILE_URI = "https://openidconnect.googleapis.com/v1/userinfo";

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

	// 카카오
	@Value("${oauth.kakao.client-id}")
	private String kakaoClientId;

	@Value("${oauth.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	// 구글
	@Value("${oauth.google.client-id}")
	private String googleClientId;

	@Value("${oauth.google.client-secret}")
	private String googleClientSecret;

	@Value("${oauth.google.redirect-uri}")
	private String googleRedirectUri;

	public LoginResDto login(LoginReqDto loginReqDto) {
		Member member = memberRepository.findByEmail(loginReqDto.email())
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(loginReqDto.password(), member.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		TokenDto tokenDto = tokenProvider.generateToken(member.getEmail(), String.valueOf(member.getRole()));

		return LoginResDto.of(member.getId(), tokenDto.accessToken());
	}

	@Transactional
	public TokenDto signupOrLogin(SocialType provider, String code) {
		try {
			String accessToken = requestAccessToken(provider, code);
			String[] memberInfo = requestMemberInfo(provider, accessToken);

			Member member = memberRepository.findByEmail(memberInfo[1])
				.orElseGet(() -> memberRepository.save(Member.builder()
					.socialId(memberInfo[0])
					.socialType(provider)
					.email(memberInfo[1])
					.name(memberInfo[2])
					.role(Role.USER)
					.build()));

			return tokenProvider.generateToken(member.getEmail(), member.getRole().name());

		} catch (JsonProcessingException e) {
			throw new RuntimeException("소셜 로그인 중 파싱 오류", e);
		}
	}

	private String requestAccessToken(SocialType provider, String code) throws JsonProcessingException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		String response;

		switch (provider) {
			case KAKAO -> {
				params.add("client_id", kakaoClientId);
				params.add("redirect_uri", kakaoRedirectUri);
				params.add("code", code);
				params.add("grant_type", "authorization_code");

				response = RestClient.create()
					.post()
					.uri(KAKAO_TOKEN_URI)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(params)
					.retrieve()
					.body(String.class);
			}
			case GOOGLE -> {
				params.add("client_id", googleClientId);
				params.add("client_secret", googleClientSecret);
				params.add("redirect_uri", googleRedirectUri);
				params.add("code", code);
				params.add("grant_type", "authorization_code");

				response = RestClient.create()
					.post()
					.uri(GOOGLE_TOKEN_URI)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(params)
					.retrieve()
					.body(String.class);
			}
			default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
		}

		return objectMapper.readTree(response).path("access_token").asText();
	}

	private String[] requestMemberInfo(SocialType provider, String accessToken) throws JsonProcessingException {
		String response;
		JsonNode jsonNode;

		switch (provider) {
			case KAKAO -> {
				response = RestClient.create()
					.get()
					.uri(KAKAO_PROFILE_URI)
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.body(String.class);

				jsonNode = objectMapper.readTree(response);
				String socialId = jsonNode.path("id").asText();
				String email = jsonNode.path("kakao_account").path("email").asText();
				String name = jsonNode.path("kakao_account").path("profile").path("nickname").asText();
				return new String[] {socialId, email, name};
			}
			case GOOGLE -> {
				response = RestClient.create()
					.get()
					.uri(GOOGLE_PROFILE_URI)
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.body(String.class);

				jsonNode = objectMapper.readTree(response);
				String socialId = jsonNode.path("sub").asText();
				String email = jsonNode.path("email").asText();
				String name = jsonNode.path("name").asText();
				return new String[] {socialId, email, name};
			}
			default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
		}
	}
}
