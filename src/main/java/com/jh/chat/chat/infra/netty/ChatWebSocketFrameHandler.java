package com.jh.chat.chat.infra.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.chat.chat.application.usecase.SendChatMessageUseCase;
import com.jh.chat.chat.endpoint.payload.ChatPayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ObjectMapper objectMapper;
    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final ChatWebSocketSessionRegistry sessionRegistry;

    public ChatWebSocketFrameHandler(ObjectMapper objectMapper,
                                     SendChatMessageUseCase sendChatMessageUseCase,
                                     ChatWebSocketSessionRegistry sessionRegistry
    ) {
        this.objectMapper = objectMapper;
        this.sendChatMessageUseCase = sendChatMessageUseCase;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent
                && ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).get() != null
        ) {
            sessionRegistry.register(ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).get(), ctx.channel());
        }

        super.userEventTriggered(ctx, event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long memberId = ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).get();
        if (memberId != null) {
            sessionRegistry.unregister(memberId, ctx.channel());
        }

        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        Long senderMemberId = ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).get();
        if (senderMemberId == null) {
            ctx.close();
            return;
        }

        ChatPayload.WebSocketSendRequest request = objectMapper.readValue(
                frame.text(),
                ChatPayload.WebSocketSendRequest.class
        );
        sendChatMessageUseCase.execute(request.toRequest(senderMemberId));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Chat websocket error. channelId={}", ctx.channel().id(), cause);
        ctx.writeAndFlush(new TextWebSocketFrame(errorMessage(cause)))
                .addListener(future -> ctx.close());
    }

    private String errorMessage(Throwable cause) {
        try {
            return objectMapper.writeValueAsString(
                    java.util.Map.of(
                            "code", "CHAT_ERROR",
                            "message", cause.getMessage() == null ? "chat error" : cause.getMessage()
                    )
            );
        } catch (Exception ignored) {
            return "{\"code\":\"CHAT_ERROR\",\"message\":\"chat error\"}";
        }
    }
}
