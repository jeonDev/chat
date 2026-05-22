package com.jh.chat.member.endpoint.payload;

import com.jh.chat.member.application.service.request.MemberProfileUpdateRequest;
import com.jh.chat.member.application.service.response.MemberProfileResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberProfilePayload() {

    @Schema(name = "MemberProfilePayload.UpdateRequest")
    public record UpdateRequest(
            @Schema(description = "회원 이름", example = "전종현")
            String name,
            @Schema(description = "연락처", example = "010-1234-5678")
            String phone
    ) {
        public MemberProfileUpdateRequest toRequest(Long memberId) {
            return new MemberProfileUpdateRequest(
                    memberId,
                    name,
                    phone
            );
        }
    }

    @Schema(name = "MemberProfilePayload.Response")
    public record Response(
            Long memberId,
            String loginId,
            String name,
            String phone
    ) {
        public static Response of(MemberProfileResult result) {
            return new Response(
                    result.memberId(),
                    result.loginId(),
                    result.name(),
                    result.phone()
            );
        }
    }
}

