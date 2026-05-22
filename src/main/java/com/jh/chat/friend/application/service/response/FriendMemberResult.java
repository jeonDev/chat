package com.jh.chat.friend.application.service.response;

import com.jh.chat.friend.domain.entity.Friend;
import com.jh.chat.member.domain.entity.Member;
import java.time.LocalDateTime;

public record FriendMemberResult(
        Long memberId,
        String name,
        String phone,
        LocalDateTime addedAt
) {
    public static FriendMemberResult friendOf(Friend friend) {
        return of(friend.getFriend(), friend.getCreatedAt());
    }

    public static FriendMemberResult requesterOf(Friend friend) {
        return of(friend.getRequester(), friend.getCreatedAt());
    }

    private static FriendMemberResult of(Member member, LocalDateTime addedAt) {
        return new FriendMemberResult(
                member.getId(),
                member.getName(),
                member.getPhone(),
                addedAt
        );
    }
}

