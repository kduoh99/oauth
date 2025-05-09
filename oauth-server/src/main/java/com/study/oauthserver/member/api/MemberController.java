package com.study.oauthserver.member.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.oauthserver.global.auth.dto.TokenDto;
import com.study.oauthserver.member.api.dto.request.MemberLoginReqDto;
import com.study.oauthserver.member.api.dto.request.MemberSaveReqDto;
import com.study.oauthserver.member.api.dto.response.MemberLoginResDto;
import com.study.oauthserver.member.application.GoogleService;
import com.study.oauthserver.member.application.KakaoService;
import com.study.oauthserver.member.application.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;
	private final GoogleService googleService;
	private final KakaoService kakaoService;

	@PostMapping("/save")
	public ResponseEntity<Long> save(@RequestBody MemberSaveReqDto memberSaveReqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(memberService.save(memberSaveReqDto));
	}

	@PostMapping("/login")
	public ResponseEntity<MemberLoginResDto> login(@RequestBody MemberLoginReqDto memberLoginReqDto) {
		return ResponseEntity.ok(memberService.login(memberLoginReqDto));
	}

	@PostMapping("/callback/google")
	public ResponseEntity<TokenDto> googleCallback(@RequestParam String code) {
		return ResponseEntity.ok(googleService.signupOrLogin(code));
	}

	@PostMapping("/callback/kakao")
	public ResponseEntity<TokenDto> kakaoCallback(@RequestParam String code) {
		return ResponseEntity.ok(kakaoService.signupOrLogin(code));
	}
}
