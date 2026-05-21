package com.jh.chat.chat.infra.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;

public class ChatWebSocketAuthHandler extends ChannelInboundHandlerAdapter {

    private final String path;

    public ChatWebSocketAuthHandler(String path) {
        this.path = path;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            if (!path.equals(decoder.path())) {
                ctx.close();
                return;
            }

            List<String> memberIds = decoder.parameters().get("memberId");
            if (memberIds == null || memberIds.isEmpty()) {
                ctx.close();
                return;
            }

            try {
                ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).set(Long.parseLong(memberIds.getFirst()));
            } catch (NumberFormatException exception) {
                ctx.close();
                return;
            }
        }

        super.channelRead(ctx, msg);
    }
}
