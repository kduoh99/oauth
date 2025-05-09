package com.study.oauthserver.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.oauthserver.member.api.dto.request.MemberSaveReqDto;
import com.study.oauthserver.member.domain.Member;
import com.study.oauthserver.member.domain.Role;
import com.study.oauthserver.member.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Long save(MemberSaveReqDto memberSaveReqDto) {
		if (memberRepository.findByEmail(memberSaveReqDto.email()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}

		Member member = memberRepository.save(Member.builder()
			.email(memberSaveReqDto.email())
			.password(passwordEncoder.encode(memberSaveReqDto.password()))
			.role(Role.USER)
			.build());

		return member.getId();
	}

}
