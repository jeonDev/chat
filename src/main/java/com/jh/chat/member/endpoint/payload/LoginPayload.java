package com.jh.chat.member.endpoint.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginPayload() {
    @Schema(name = "LoginPayload.Request")
    public record Request(
            @Schema(description = "로그인 ID", example = "jhjeon")
            String loginId,
            @Schema(description = "비밀번호", example = "password1234")
            String password
    ) {

    }

    @Schema(name = "LoginPayload.Response")
    public record Response(
            @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
            String accessToken
    ) {

        public static Response of(String accessToken) {
            return new Response(accessToken);
        }
    }
}
