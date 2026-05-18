package com.jh.chat.member.endpoint.payload;

public record LoginPayload() {
    public record Request(
            String loginId,
            String password
    ) {

    }

    public record Response(
            String accessToken
    ) {

        public static Response of(String accessToken) {
            return new Response(accessToken);
        }
    }
}
