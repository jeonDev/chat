package com.jh.chat.room.domain.repository;

import com.jh.chat.room.domain.entity.ChatRoom;
import com.jh.chat.room.domain.entity.ChatRoomType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomTypeAndRoomKey(ChatRoomType roomType, String roomKey);
}

