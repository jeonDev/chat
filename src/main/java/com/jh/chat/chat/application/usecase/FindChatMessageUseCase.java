package com.jh.chat.chat.application.usecase;

import com.jh.chat.chat.application.service.ChatService;
import com.jh.chat.chat.application.service.response.ChatDeliveryMessage;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FindChatMessageUseCase {

    private final ChatService chatService;

    public FindChatMessageUseCase(ChatService chatService) {
        this.chatService = chatService;
    }

    public List<ChatDeliveryMessage> execute(Long roomId) {
        return chatService.getRoomMessages(roomId);
    }
}

