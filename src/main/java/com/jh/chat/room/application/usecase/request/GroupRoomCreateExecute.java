package com.jh.chat.room.application.usecase.request;

import java.util.List;

public record GroupRoomCreateExecute(
        String name,
        Long ownerMemberId,
        List<Long> memberIds
) {
}

