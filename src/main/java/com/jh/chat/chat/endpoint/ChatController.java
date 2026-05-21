package com.jh.chat.chat.endpoint;

import com.jh.chat.chat.application.usecase.FindChatMessageUseCase;
import com.jh.chat.chat.endpoint.payload.ChatPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms/{roomId}/messages")
@Tag(name = "Chat", description = "채팅 메시지 API")
public class ChatController {

    private final FindChatMessageUseCase findChatMessageUseCase;

    @Operation(summary = "채팅 메시지 목록 조회", description = "채팅방 메시지를 발송 순서대로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ChatPayload.MessageResponse>> getMessages(@PathVariable Long roomId) {
        var response = findChatMessageUseCase.execute(roomId)
                .stream()
                .map(ChatPayload.MessageResponse::of)
                .toList();

        return ResponseEntity.ok(response);
    }
}

