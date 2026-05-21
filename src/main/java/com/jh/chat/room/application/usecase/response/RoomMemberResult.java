package com.jh.chat.room.application.usecase.response;

import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;

public record RoomMemberResult(
        Long memberId,
        String name,
        ChatRoomMemberStatus status
) {
    public static RoomMemberResult of(ChatRoomMember roomMember) {
        return new RoomMemberResult(
                roomMember.getMember().getId(),
                roomMember.getMember().getName(),
                roomMember.getStatus()
        );
    }
}

