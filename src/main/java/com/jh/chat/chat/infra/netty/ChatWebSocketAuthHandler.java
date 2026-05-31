package com.jh.chat.chat.infra.netty;

import com.jh.chat.common.security.JwtTokenProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.Arrays;

public class ChatWebSocketAuthHandler extends ChannelInboundHandlerAdapter {

    public static final String JWT_SUBPROTOCOL = "jwt";
    private static final String BEARER_PREFIX = "Bearer ";

    private final String path;
    private final JwtTokenProvider jwtTokenProvider;

    public ChatWebSocketAuthHandler(String path,
                                    JwtTokenProvider jwtTokenProvider
    ) {
        this.path = path;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            if (!path.equals(new QueryStringDecoder(request.uri()).path())) {
                reject(ctx, msg);
                return;
            }

            String token = resolveToken(request);
            if (token == null) {
                reject(ctx, msg);
                return;
            }

            try {
                Long memberId = Long.valueOf(jwtTokenProvider.getSubject(token));
                ctx.channel().attr(ChatWebSocketAttributes.MEMBER_ID).set(memberId);
            } catch (RuntimeException exception) {
                reject(ctx, msg);
                return;
            }
        }

        super.channelRead(ctx, msg);
    }

    private String resolveToken(FullHttpRequest request) {
        String authorization = request.headers().get(HttpHeaderNames.AUTHORIZATION);
        if (authorization != null
                && authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        String subprotocols = request.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        if (subprotocols == null) {
            return null;
        }

        var values = Arrays.stream(subprotocols.split(","))
                .map(String::trim)
                .toList();
        int jwtProtocolIndex = values.indexOf(JWT_SUBPROTOCOL);
        if (jwtProtocolIndex < 0 || jwtProtocolIndex + 1 >= values.size()) {
            return null;
        }

        String token = values.get(jwtProtocolIndex + 1);
        return token.isBlank() ? null : token;
    }

    private void reject(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
        ctx.close();
    }
}
