package com.jh.chat.chat.domain.repository;

import com.jh.chat.chat.domain.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoom_IdOrderBySentAtAsc(Long roomId);
}

