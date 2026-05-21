package com.jh.chat.room.application.usecase.request;

import java.util.List;

public record GroupRoomInviteExecute(
        Long roomId,
        Long requesterMemberId,
        List<Long> memberIds
) {
}

