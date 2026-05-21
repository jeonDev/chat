package com.jh.chat.chat.endpoint.payload;

import com.jh.chat.chat.application.service.request.ChatSendRequest;
import com.jh.chat.chat.application.service.response.ChatDeliveryMessage;
import com.jh.chat.chat.application.service.response.ChatSendResult;
import com.jh.chat.chat.domain.entity.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ChatPayload() {

    @Schema(name = "ChatPayload.WebSocketSendRequest")
    public record WebSocketSendRequest(
            @Schema(description = "채팅방 ID", example = "1")
            Long roomId,
            @Schema(description = "메시지 타입", example = "TEXT")
            ChatMessageType messageType,
            @Schema(description = "JSONB로 저장할 메시지 내용", example = "{\"text\":\"hello\"}")
            Map<String, Object> content
    ) {
        public ChatSendRequest toRequest(Long senderMemberId) {
            return new ChatSendRequest(
                    roomId,
                    senderMemberId,
                    messageType,
                    content
            );
        }
    }

    @Schema(name = "ChatPayload.WebSocketSendResponse")
    public record WebSocketSendResponse(
            MessageResponse message,
            List<Long> onlineMemberIds,
            List<Long> offlineMemberIds
    ) {
        public static WebSocketSendResponse of(ChatSendResult result) {
            return new WebSocketSendResponse(
                    MessageResponse.of(result.message()),
                    result.onlineMemberIds(),
                    result.offlineMemberIds()
            );
        }
    }

    @Schema(name = "ChatPayload.MessageResponse")
    public record MessageResponse(
            Long messageId,
            Long roomId,
            Long senderMemberId,
            String messageType,
            Map<String, Object> content,
            LocalDateTime sentAt
    ) {
        public static MessageResponse of(ChatDeliveryMessage message) {
            return new MessageResponse(
                    message.messageId(),
                    message.roomId(),
                    message.senderMemberId(),
                    message.messageType(),
                    message.content(),
                    message.sentAt()
            );
        }
    }
}

