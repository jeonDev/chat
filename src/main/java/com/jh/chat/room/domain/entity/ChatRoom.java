package com.jh.chat.room.domain.entity;

import com.jh.chat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "CHAT_ROOM")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROOM_TYPE", length = 20, nullable = false)
    private ChatRoomType roomType;

    @Column(name = "ROOM_KEY", length = 500, nullable = false, unique = true)
    private String roomKey;

    public static ChatRoom direct(Long firstMemberId, Long secondMemberId) {
        return new ChatRoom(
                null,
                null,
                ChatRoomType.DIRECT,
                directRoomKey(firstMemberId, secondMemberId)
        );
    }

    public static ChatRoom group(String name, Collection<Long> memberIds) {
        return new ChatRoom(
                null,
                name,
                ChatRoomType.GROUP,
                groupRoomKey(memberIds)
        );
    }

    public static String directRoomKey(Long firstMemberId, Long secondMemberId) {
        long min = Math.min(firstMemberId, secondMemberId);
        long max = Math.max(firstMemberId, secondMemberId);
        return "DIRECT:%d:%d".formatted(min, max);
    }

    public static String groupRoomKey(Collection<Long> memberIds) {
        String normalizedMemberIds = memberIds.stream()
                .distinct()
                .sorted(Comparator.naturalOrder())
                .map(String::valueOf)
                .collect(Collectors.joining(":"));

        return "GROUP:%s".formatted(normalizedMemberIds);
    }

    public boolean isGroup() {
        return roomType == ChatRoomType.GROUP;
    }

    public void rename(String name) {
        this.name = name;
    }
}

