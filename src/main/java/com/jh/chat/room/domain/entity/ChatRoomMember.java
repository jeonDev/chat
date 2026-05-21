package com.jh.chat.room.domain.entity;

import com.jh.chat.common.entity.BaseEntity;
import com.jh.chat.member.domain.entity.Member;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "CHAT_ROOM_MEMBER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CHAT_ROOM_MEMBER",
                        columnNames = {"CHAT_ROOM_ID", "MEMBER_ID"}
                )
        }
)
public class ChatRoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private ChatRoomMemberStatus status;

    @Column(name = "JOINED_AT")
    private LocalDateTime joinedAt;

    @Column(name = "LEFT_AT")
    private LocalDateTime leftAt;

    public static ChatRoomMember join(ChatRoom chatRoom, Member member) {
        LocalDateTime now = LocalDateTime.now();
        return new ChatRoomMember(
                null,
                chatRoom,
                member,
                ChatRoomMemberStatus.JOINED,
                now,
                null
        );
    }

    public void rejoin() {
        this.status = ChatRoomMemberStatus.JOINED;
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
    }

    public void leave() {
        if (status == ChatRoomMemberStatus.LEFT) {
            return;
        }

        this.status = ChatRoomMemberStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public boolean isJoined() {
        return status == ChatRoomMemberStatus.JOINED;
    }
}

