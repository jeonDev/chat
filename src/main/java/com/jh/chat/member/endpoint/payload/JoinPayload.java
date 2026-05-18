package com.jh.chat.member.endpoint.payload;

import com.jh.chat.member.application.usecase.request.JoinExecute;
import io.swagger.v3.oas.annotations.media.Schema;

public record JoinPayload() {

    public record Request(
            @Schema(description = "로그인 ID", example = "jh")
            String loginId,
            @Schema(description = "비밀번호", example = "password1234")
            String password,
            @Schema(description = "회원 이름", example = "정지훈")
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

    @Schema(description = "회원가입 응답")
    public record Response() {

    }
}
