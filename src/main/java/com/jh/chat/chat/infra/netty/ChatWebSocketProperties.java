package com.jh.chat.chat.infra.netty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "chat.netty.websocket")
public class ChatWebSocketProperties {

    private boolean enabled = true;

    private int port = 8090;

    private String path = "/ws/chat";
}

