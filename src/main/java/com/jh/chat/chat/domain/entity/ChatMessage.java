package com.jh.chat.chat.domain.entity;

import com.jh.chat.common.entity.BaseEntity;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.room.domain.entity.ChatRoom;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "CHAT_MESSAGE")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_MEMBER_ID", nullable = false)
    private Member sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_TYPE", length = 20, nullable = false)
    private ChatMessageType messageType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "CONTENT", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> content;

    @Column(name = "SENT_AT", nullable = false)
    private LocalDateTime sentAt;

    public static ChatMessage of(ChatRoom chatRoom,
                                 Member sender,
                                 ChatMessageType messageType,
                                 Map<String, Object> content
    ) {
        return new ChatMessage(
                null,
                chatRoom,
                sender,
                messageType,
                content,
                LocalDateTime.now()
        );
    }
}

