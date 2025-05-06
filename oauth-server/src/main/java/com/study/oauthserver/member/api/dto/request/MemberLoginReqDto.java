package com.study.oauthserver.member.api.dto.request;

public record MemberLoginReqDto(
	String email,
	String password
) {
}
