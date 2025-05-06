package com.study.oauthserver.member.api.dto.request;

public record MemberSaveReqDto(
	String email,
	String password
) {
}
