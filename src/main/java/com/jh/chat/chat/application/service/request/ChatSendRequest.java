package com.jh.chat.chat.application.service.request;

import com.jh.chat.chat.domain.entity.ChatMessageType;
import java.util.Map;

public record ChatSendRequest(
        Long roomId,
        Long senderMemberId,
        ChatMessageType messageType,
        Map<String, Object> content
) {
}

