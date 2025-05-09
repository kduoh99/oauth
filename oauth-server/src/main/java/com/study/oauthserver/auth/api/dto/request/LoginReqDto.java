package com.study.oauthserver.auth.api.dto.request;

public record LoginReqDto(
	String email,
	String password
) {
}
