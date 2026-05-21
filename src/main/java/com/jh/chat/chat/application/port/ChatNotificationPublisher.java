package com.jh.chat.chat.application.port;

import com.jh.chat.chat.domain.entity.ChatNotification;

public interface ChatNotificationPublisher {

    void publish(ChatNotification notification);
}

