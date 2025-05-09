package com.study.oauthserver.member.api.dto.request;

public record MemberSaveReqDto(
	String name,
	String email,
	String password
) {
}
