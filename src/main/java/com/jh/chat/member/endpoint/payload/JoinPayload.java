package com.jh.chat.member.endpoint.payload;

import com.jh.chat.member.application.usecase.request.JoinExecute;
import io.swagger.v3.oas.annotations.media.Schema;

public record JoinPayload() {

    @Schema(name = "JoinPayload.Request")
    public record Request(
            @Schema(description = "로그인 ID", example = "jhjeon")
            String loginId,
            @Schema(description = "비밀번호", example = "password1234")
            String password,
            @Schema(description = "회원 이름", example = "전종현")
            String name
    ) {
        public JoinExecute toExecute() {
            return new JoinExecute(
                    loginId,
                    password,
                    name
            );
        }
    }

    @Schema(name = "JoinPayload.Response")
    public record Response() {

    }
}
