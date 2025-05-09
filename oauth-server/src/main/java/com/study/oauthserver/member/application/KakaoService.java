package com.study.oauthserver.member.application;

import org.springframework.beans.factory.annotation.Value;
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
import com.study.oauthserver.member.domain.Member;
import com.study.oauthserver.member.domain.Role;
import com.study.oauthserver.member.domain.SocialType;
import com.study.oauthserver.member.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {

	private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
	private static final String PROFILE_URI = "https://kapi.kakao.com/v2/user/me";

	private final MemberRepository memberRepository;
	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

	@Value("${oauth.kakao.client-id}")
	private String kakaoClientId;

	@Value("${oauth.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Transactional
	public TokenDto signupOrLogin(String code) {
		try {
			String accessToken = requestAccessToken(code);
			String[] memberInfo = requestMemberInfo(accessToken);

			Member member = memberRepository.findByEmail(memberInfo[1])
				.orElseGet(() -> memberRepository.save(Member.builder()
					.socialId(memberInfo[0])
					.socialType(SocialType.KAKAO)
					.email(memberInfo[1])
					.name(memberInfo[2])
					.role(Role.USER)
					.build())
				);

			return tokenProvider.generateToken(member.getEmail(), String.valueOf(member.getRole()));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String requestAccessToken(String code) throws JsonProcessingException {
		RestClient restClient = RestClient.create();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", kakaoClientId);
		params.add("redirect_uri", kakaoRedirectUri);
		params.add("code", code);
		params.add("grant_type", "authorization_code");

		String response = restClient.post()
			.uri(TOKEN_URI)
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(params)
			.retrieve()
			.body(String.class);

		return objectMapper.readTree(response)
			.path("access_token")
			.asText();
	}

	private String[] requestMemberInfo(String accessToken) throws JsonProcessingException {
		RestClient restClient = RestClient.create();

		String response = restClient.get()
			.uri(PROFILE_URI)
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.body(String.class);

		JsonNode jsonNode = objectMapper.readTree(response);
		String socialId = jsonNode.path("id").asText();
		String email = jsonNode.path("kakao_account").path("email").asText();
		String name = jsonNode.path("kakao_account").path("profile").path("nickname").asText();

		return new String[] {socialId, email, name};
	}
}
