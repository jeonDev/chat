package com.jh.chat.chat.application.service.response;

import com.jh.chat.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.Map;

public record ChatDeliveryMessage(
        Long messageId,
        Long roomId,
        Long senderMemberId,
        String messageType,
        Map<String, Object> content,
        LocalDateTime sentAt
) {
    public static ChatDeliveryMessage of(ChatMessage message) {
        return new ChatDeliveryMessage(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getMessageType().name(),
                message.getContent(),
                message.getSentAt()
        );
    }
}

