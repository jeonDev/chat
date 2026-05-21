package com.jh.chat.chat.application.service;

import com.jh.chat.chat.application.port.ChatClientNotifier;
import com.jh.chat.chat.application.port.ChatNotificationPublisher;
import com.jh.chat.chat.application.service.request.ChatSendRequest;
import com.jh.chat.chat.application.service.response.ChatDeliveryMessage;
import com.jh.chat.chat.application.service.response.ChatSendResult;
import com.jh.chat.chat.domain.entity.ChatMessage;
import com.jh.chat.chat.domain.entity.ChatMessageType;
import com.jh.chat.chat.domain.entity.ChatNotification;
import com.jh.chat.chat.domain.repository.JpaChatMessageRepository;
import com.jh.chat.chat.domain.repository.JpaChatNotificationRepository;
import com.jh.chat.common.exception.NotFoundException;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import com.jh.chat.room.domain.entity.ChatRoom;
import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;
import com.jh.chat.room.domain.repository.JpaChatRoomMemberRepository;
import com.jh.chat.room.domain.repository.JpaChatRoomRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

    private final JpaChatRoomRepository chatRoomRepository;
    private final JpaChatRoomMemberRepository chatRoomMemberRepository;
    private final JpaMemberRepository memberRepository;
    private final JpaChatMessageRepository chatMessageRepository;
    private final JpaChatNotificationRepository chatNotificationRepository;
    private final ChatClientNotifier chatClientNotifier;
    private final ChatNotificationPublisher chatNotificationPublisher;

    public ChatService(JpaChatRoomRepository chatRoomRepository,
                       JpaChatRoomMemberRepository chatRoomMemberRepository,
                       JpaMemberRepository memberRepository,
                       JpaChatMessageRepository chatMessageRepository,
                       JpaChatNotificationRepository chatNotificationRepository,
                       ChatClientNotifier chatClientNotifier,
                       ChatNotificationPublisher chatNotificationPublisher
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.memberRepository = memberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatNotificationRepository = chatNotificationRepository;
        this.chatClientNotifier = chatClientNotifier;
        this.chatNotificationPublisher = chatNotificationPublisher;
    }

    @Transactional
    public ChatSendResult send(ChatSendRequest request) {
        validateSendRequest(request);

        ChatRoom chatRoom = chatRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. roomId=%d".formatted(request.roomId())));
        Member sender = memberRepository.findById(request.senderMemberId())
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. memberId=%d".formatted(request.senderMemberId())));

        ChatRoomMember senderRoomMember = chatRoomMemberRepository
                .findByChatRoom_IdAndMember_Id(chatRoom.getId(), sender.getId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방 참여자만 메시지를 보낼 수 있습니다."));
        if (!senderRoomMember.isJoined()) {
            throw new IllegalArgumentException("채팅방에 참여 중인 회원만 메시지를 보낼 수 있습니다.");
        }

        ChatMessage chatMessage = chatMessageRepository.save(
                ChatMessage.of(chatRoom, sender, request.messageType(), request.content())
        );
        ChatDeliveryMessage deliveryMessage = ChatDeliveryMessage.of(chatMessage);

        List<Long> onlineMemberIds = new ArrayList<>();
        List<Long> offlineMemberIds = new ArrayList<>();
        List<ChatRoomMember> roomMembers = chatRoomMemberRepository
                .findAllByChatRoom_IdAndStatus(chatRoom.getId(), ChatRoomMemberStatus.JOINED);

        for (ChatRoomMember roomMember : roomMembers) {
            Long receiverMemberId = roomMember.getMember().getId();
            if (chatClientNotifier.isConnected(receiverMemberId)) {
                chatClientNotifier.send(receiverMemberId, deliveryMessage);
                onlineMemberIds.add(receiverMemberId);
                continue;
            }

            if (!receiverMemberId.equals(sender.getId())) {
                ChatNotification notification = chatNotificationRepository.save(
                        ChatNotification.pending(chatMessage, roomMember.getMember(), notificationPayload(deliveryMessage))
                );
                chatNotificationPublisher.publish(notification);
                offlineMemberIds.add(receiverMemberId);
            }
        }

        return new ChatSendResult(deliveryMessage, onlineMemberIds, offlineMemberIds);
    }

    @Transactional(readOnly = true)
    public List<ChatDeliveryMessage> getRoomMessages(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (!chatRoomRepository.existsById(roomId)) {
            throw new NotFoundException("채팅방을 찾을 수 없습니다. roomId=%d".formatted(roomId));
        }

        return chatMessageRepository.findAllByChatRoom_IdOrderBySentAtAsc(roomId)
                .stream()
                .map(ChatDeliveryMessage::of)
                .toList();
    }

    private void validateSendRequest(ChatSendRequest request) {
        if (request.roomId() == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }
        if (request.senderMemberId() == null) {
            throw new IllegalArgumentException("senderMemberId는 필수입니다.");
        }
        if (request.messageType() == null) {
            throw new IllegalArgumentException("messageType은 필수입니다.");
        }
        if (request.content() == null || request.content().isEmpty()) {
            throw new IllegalArgumentException("content는 필수입니다.");
        }
        if (request.messageType() == ChatMessageType.TEXT && isBlankText(request.content())) {
            throw new IllegalArgumentException("TEXT 메시지는 content.text가 필요합니다.");
        }
    }

    private boolean isBlankText(Map<String, Object> content) {
        Object text = content.get("text");
        return !(text instanceof String value) || value.isBlank();
    }

    private Map<String, Object> notificationPayload(ChatDeliveryMessage message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messageId", message.messageId());
        payload.put("roomId", message.roomId());
        payload.put("senderMemberId", message.senderMemberId());
        payload.put("messageType", message.messageType());
        payload.put("content", message.content());
        payload.put("sentAt", message.sentAt().toString());
        return payload;
    }
}

