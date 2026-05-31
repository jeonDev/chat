package com.jh.chat.friend.endpoint.payload;

import com.jh.chat.friend.application.service.response.FriendMemberResult;
import com.jh.chat.friend.application.service.response.FriendSearchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record FriendPayload() {

    @Schema(name = "FriendPayload.AddRequest")
    public record AddRequest(
            @Schema(description = "친구로 추가할 회원 ID", example = "2")
            Long friendMemberId
    ) {
    }

    @Schema(name = "FriendPayload.SearchResponse")
    public record SearchResponse(
            Long memberId,
            String name,
            String phone,
            boolean alreadyFriend
    ) {
        public static SearchResponse of(FriendSearchResult result) {
            return new SearchResponse(
                    result.memberId(),
                    result.name(),
                    result.phone(),
                    result.alreadyFriend()
            );
        }
    }

    @Schema(name = "FriendPayload.MemberResponse")
    public record MemberResponse(
            Long memberId,
            String name,
            String phone,
            LocalDateTime addedAt
    ) {
        public static MemberResponse of(FriendMemberResult result) {
            return new MemberResponse(
                    result.memberId(),
                    result.name(),
                    result.phone(),
                    result.addedAt()
            );
        }
    }
}
