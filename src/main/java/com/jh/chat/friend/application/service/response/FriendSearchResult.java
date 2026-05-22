package com.jh.chat.friend.application.service.response;

import com.jh.chat.member.domain.entity.Member;

public record FriendSearchResult(
        Long memberId,
        String name,
        String phone,
        boolean alreadyFriend
) {
    public static FriendSearchResult of(Member member, boolean alreadyFriend) {
        return new FriendSearchResult(
                member.getId(),
                member.getName(),
                member.getPhone(),
                alreadyFriend
        );
    }
}

