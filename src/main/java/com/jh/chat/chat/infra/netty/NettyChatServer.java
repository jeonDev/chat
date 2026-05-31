package com.jh.chat.chat.infra.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.chat.chat.application.usecase.SendChatMessageUseCase;
import com.jh.chat.common.security.JwtTokenProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "chat.netty.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NettyChatServer implements SmartLifecycle {

    private final ChatWebSocketProperties properties;
    private final ObjectMapper objectMapper;
    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final ChatWebSocketSessionRegistry sessionRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private volatile boolean running;

    public NettyChatServer(ChatWebSocketProperties properties,
                           ObjectMapper objectMapper,
                           SendChatMessageUseCase sendChatMessageUseCase,
                           ChatWebSocketSessionRegistry sessionRegistry,
                           JwtTokenProvider jwtTokenProvider
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.sendChatMessageUseCase = sendChatMessageUseCase;
        this.sessionRegistry = sessionRegistry;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void start() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            serverChannel = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new ChatWebSocketAuthHandler(properties.getPath(), jwtTokenProvider))
                                    .addLast(new WebSocketServerProtocolHandler(
                                            webSocketProtocolConfig(properties.getPath())
                                    ))
                                    .addLast(new ChatWebSocketFrameHandler(
                                            objectMapper,
                                            sendChatMessageUseCase,
                                            sessionRegistry
                                    ));
                        }
                    })
                    .bind(properties.getPort())
                    .sync()
                    .channel();
            running = true;
            log.info("Netty chat websocket server started. port={}, path={}",
                    properties.getPort(),
                    properties.getPath()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            stop();
            throw new IllegalStateException("Netty chat websocket server start interrupted.", exception);
        }
    }

    @Override
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    static WebSocketServerProtocolConfig webSocketProtocolConfig(String path) {
        return WebSocketServerProtocolConfig.newBuilder()
                .websocketPath(path)
                .subprotocols(ChatWebSocketAuthHandler.JWT_SUBPROTOCOL)
                .checkStartsWith(true)
                .decoderConfig(WebSocketDecoderConfig.newBuilder()
                        .allowExtensions(true)
                        .build())
                .build();
    }
}
