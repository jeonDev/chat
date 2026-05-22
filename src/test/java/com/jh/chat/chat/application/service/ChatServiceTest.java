package com.jh.chat.chat.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.chat.chat.application.service.request.ChatSendRequest;
import com.jh.chat.chat.application.service.response.ChatSendResult;
import com.jh.chat.chat.domain.entity.ChatMessageType;
import com.jh.chat.chat.domain.entity.ChatNotificationStatus;
import com.jh.chat.chat.domain.repository.JpaChatMessageRepository;
import com.jh.chat.chat.domain.repository.JpaChatNotificationRepository;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import com.jh.chat.room.application.service.RoomService;
import com.jh.chat.room.application.usecase.request.GroupRoomCreateExecute;
import com.jh.chat.room.application.usecase.response.RoomResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private JpaMemberRepository memberRepository;

    @Autowired
    private JpaChatMessageRepository chatMessageRepository;

    @Autowired
    private JpaChatNotificationRepository chatNotificationRepository;

    @Test
    void send_savesMessageContentAndOfflineNotifications() {
        Member sender = saveMember("chat-sender");
        Member receiver = saveMember("chat-receiver");
        RoomResult room = roomService.createGroupRoom(
                new GroupRoomCreateExecute(
                        "chat-room",
                        sender.getId(),
                        List.of(receiver.getId())
                )
        );

        ChatSendResult result = chatService.send(
                new ChatSendRequest(
                        room.roomId(),
                        sender.getId(),
                        ChatMessageType.TEXT,
                        Map.of("text", "hello")
                )
        );

        assertThat(result.message().content()).containsEntry("text", "hello");
        assertThat(result.offlineMemberIds()).containsExactly(receiver.getId());

        var messages = chatMessageRepository.findAllByChatRoomIdOrderBySentAtAsc(room.roomId());
        assertThat(messages).hasSize(1);
        assertThat(messages.getFirst().getContent()).containsEntry("text", "hello");

        var notifications = chatNotificationRepository
                .findAllByReceiverIdAndStatus(receiver.getId(), ChatNotificationStatus.PENDING);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.getFirst().getPayload()).containsEntry("messageType", "TEXT");
    }

    private Member saveMember(String loginId) {
        return memberRepository.save(Member.of(loginId, "password", loginId));
    }
}
