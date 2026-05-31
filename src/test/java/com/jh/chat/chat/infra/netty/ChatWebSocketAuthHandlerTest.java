package com.jh.chat.chat.infra.netty;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.chat.common.security.AppSecurityProperties;
import com.jh.chat.common.security.JwtTokenProvider;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class ChatWebSocketAuthHandlerTest {

    private static final String SECRET = "01234567890123456789012345678901";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        AppSecurityProperties properties = new AppSecurityProperties();
        properties.getJwt().setSecret(SECRET);
        properties.getJwt().setExpirationMillis(3_600_000);
        jwtTokenProvider = new JwtTokenProvider(properties, new JsonMapper());
    }

    @Test
    void authenticatesMemberFromBearerToken() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));
        FullHttpRequest request = request("/ws/chat");
        request.headers().set(HttpHeaderNames.AUTHORIZATION, "Bearer " + jwtTokenProvider.createToken("1"));

        assertThat(channel.writeInbound(request)).isTrue();
        assertThat(channel.attr(ChatWebSocketAttributes.MEMBER_ID).get()).isEqualTo(1L);

        ReferenceCountUtil.release(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void acceptsQueryStringWhenBearerTokenIsValid() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));
        FullHttpRequest request = request("/ws/chat?client=web");
        request.headers().set(HttpHeaderNames.AUTHORIZATION, "Bearer " + jwtTokenProvider.createToken("1"));

        assertThat(channel.writeInbound(request)).isTrue();
        assertThat(channel.attr(ChatWebSocketAttributes.MEMBER_ID).get()).isEqualTo(1L);

        ReferenceCountUtil.release(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void authenticatesBrowserClientFromJwtSubprotocol() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));
        FullHttpRequest request = request("/ws/chat");
        request.headers().set(
                HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL,
                "jwt, " + jwtTokenProvider.createToken("1")
        );

        assertThat(channel.writeInbound(request)).isTrue();
        assertThat(channel.attr(ChatWebSocketAttributes.MEMBER_ID).get()).isEqualTo(1L);

        ReferenceCountUtil.release(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void configuresProtocolHandlerToAcceptQueryString() {
        assertThat(NettyChatServer.webSocketProtocolConfig("/ws/chat").checkStartsWith()).isTrue();
        assertThat(NettyChatServer.webSocketProtocolConfig("/ws/chat").subprotocols()).isEqualTo("jwt");
    }

    @Test
    void rejectsMissingBearerToken() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));

        assertThat(channel.writeInbound(request("/ws/chat"))).isFalse();
        assertThat(channel.isActive()).isFalse();
    }

    @Test
    void rejectsInvalidBearerToken() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));
        FullHttpRequest request = request("/ws/chat");
        request.headers().set(HttpHeaderNames.AUTHORIZATION, "Bearer invalid-token");

        assertThat(channel.writeInbound(request)).isFalse();
        assertThat(channel.isActive()).isFalse();
    }

    @Test
    void rejectsUnexpectedPath() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChatWebSocketAuthHandler("/ws/chat", jwtTokenProvider));
        FullHttpRequest request = request("/ws/other");
        request.headers().set(HttpHeaderNames.AUTHORIZATION, "Bearer " + jwtTokenProvider.createToken("1"));

        assertThat(channel.writeInbound(request)).isFalse();
        assertThat(channel.isActive()).isFalse();
    }

    private FullHttpRequest request(String uri) {
        return new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
    }
}
