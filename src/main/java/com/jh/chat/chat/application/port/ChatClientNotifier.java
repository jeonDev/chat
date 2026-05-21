package com.jh.chat.chat.application.port;

import com.jh.chat.chat.application.service.response.ChatDeliveryMessage;

public interface ChatClientNotifier {

    boolean isConnected(Long memberId);

    void send(Long memberId, ChatDeliveryMessage message);
}

