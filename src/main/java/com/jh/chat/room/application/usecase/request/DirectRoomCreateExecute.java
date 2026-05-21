package com.jh.chat.room.application.usecase.request;

public record DirectRoomCreateExecute(
        Long requesterMemberId,
        Long partnerMemberId
) {
}

