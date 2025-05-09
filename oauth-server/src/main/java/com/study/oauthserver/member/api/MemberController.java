package com.study.oauthserver.member.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.oauthserver.member.api.dto.request.MemberSaveReqDto;
import com.study.oauthserver.member.application.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/save")
	public ResponseEntity<Long> save(@RequestBody MemberSaveReqDto memberSaveReqDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(memberService.save(memberSaveReqDto));
	}
}
