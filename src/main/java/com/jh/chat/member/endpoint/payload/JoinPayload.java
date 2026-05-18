package com.jh.chat.member.endpoint.payload;

import com.jh.chat.member.application.usecase.request.JoinExecute;

public record JoinPayload() {

    public record Request(
            String loginId,
            String password,
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

    public record Response(
    ) {

    }
}
