package com.jh.chat.chat.domain.repository;

import com.jh.chat.chat.domain.entity.ChatNotification;
import com.jh.chat.chat.domain.entity.ChatNotificationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatNotificationRepository extends JpaRepository<ChatNotification, Long> {

    List<ChatNotification> findAllByReceiverIdAndStatus(Long receiverId, ChatNotificationStatus status);
}
