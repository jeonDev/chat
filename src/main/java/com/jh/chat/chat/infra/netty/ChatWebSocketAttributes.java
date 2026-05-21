package com.jh.chat.chat.infra.netty;

import io.netty.util.AttributeKey;

public final class ChatWebSocketAttributes {

    public static final AttributeKey<Long> MEMBER_ID = AttributeKey.valueOf("memberId");

    private ChatWebSocketAttributes() {
    }
}

