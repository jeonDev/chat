package com.jh.chat.chat.application.service.response;

import java.util.List;

public record ChatSendResult(
        ChatDeliveryMessage message,
        List<Long> onlineMemberIds,
        List<Long> offlineMemberIds
) {
}

