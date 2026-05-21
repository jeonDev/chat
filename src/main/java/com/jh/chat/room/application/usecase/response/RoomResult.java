package com.jh.chat.room.application.usecase.response;

import com.jh.chat.room.domain.entity.ChatRoom;
import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomType;
import java.time.LocalDateTime;
import java.util.List;

public record RoomResult(
        Long roomId,
        String name,
        ChatRoomType roomType,
        List<RoomMemberResult> members,
        LocalDateTime createdAt
) {
    public static RoomResult of(ChatRoom room, List<ChatRoomMember> roomMembers) {
        return new RoomResult(
                room.getId(),
                room.getName(),
                room.getRoomType(),
                roomMembers.stream()
                        .map(RoomMemberResult::of)
                        .toList(),
                room.getCreatedAt()
        );
    }
}

