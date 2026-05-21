package com.jh.chat.chat.application.usecase;

import com.jh.chat.chat.application.service.ChatService;
import com.jh.chat.chat.application.service.request.ChatSendRequest;
import com.jh.chat.chat.application.service.response.ChatSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendChatMessageUseCase {

    private final ChatService chatService;

    public SendChatMessageUseCase(ChatService chatService) {
        this.chatService = chatService;
    }

    public ChatSendResult execute(ChatSendRequest request) {
        log.info("[채팅 메시지 전송] roomId : {}, senderMemberId : {}",
                request.roomId(),
                request.senderMemberId()
        );
        return chatService.send(request);
    }
}

