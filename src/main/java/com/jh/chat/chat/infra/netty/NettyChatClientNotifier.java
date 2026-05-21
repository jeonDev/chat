package com.jh.chat.chat.infra.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.chat.chat.application.port.ChatClientNotifier;
import com.jh.chat.chat.application.service.response.ChatDeliveryMessage;
import org.springframework.stereotype.Component;

@Component
public class NettyChatClientNotifier implements ChatClientNotifier {

    private final ChatWebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public NettyChatClientNotifier(ChatWebSocketSessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isConnected(Long memberId) {
        return sessionRegistry.isConnected(memberId);
    }

    @Override
    public void send(Long memberId, ChatDeliveryMessage message) {
        try {
            sessionRegistry.send(memberId, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("채팅 메시지 직렬화에 실패했습니다.", exception);
        }
    }
}

