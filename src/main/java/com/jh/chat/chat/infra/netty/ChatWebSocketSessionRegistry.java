package com.jh.chat.chat.infra.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ChatWebSocketSessionRegistry {

    private final Map<Long, ChannelGroup> channels = new ConcurrentHashMap<>();

    public void register(Long memberId, Channel channel) {
        channels.computeIfAbsent(memberId, ignored -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE))
                .add(channel);
    }

    public void unregister(Long memberId, Channel channel) {
        ChannelGroup group = channels.get(memberId);
        if (group == null) {
            return;
        }

        group.remove(channel);
        if (group.isEmpty()) {
            channels.remove(memberId, group);
        }
    }

    public boolean isConnected(Long memberId) {
        ChannelGroup group = channels.get(memberId);
        return group != null && !group.isEmpty();
    }

    public void send(Long memberId, String message) {
        ChannelGroup group = channels.get(memberId);
        if (group != null) {
            group.writeAndFlush(new io.netty.handler.codec.http.websocketx.TextWebSocketFrame(message));
        }
    }
}

