package com.jh.chat.room.application.usecase.request;

public record RoomLeaveExecute(
        Long roomId,
        Long memberId
) {
}

