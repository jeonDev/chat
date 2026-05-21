package com.jh.chat.chat.infra.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.chat.chat.application.port.ChatNotificationPublisher;
import com.jh.chat.chat.domain.entity.ChatNotification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresChatNotificationPublisher implements ChatNotificationPublisher {

    private static final String CHANNEL_NAME = "chat_notification";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PostgresChatNotificationPublisher(DataSource dataSource,
                                             JdbcTemplate jdbcTemplate,
                                             ObjectMapper objectMapper
    ) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(ChatNotification notification) {
        if (!isPostgres()) {
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(toPayload(notification));
            jdbcTemplate.execute((Connection connection) -> {
                try (PreparedStatement statement = connection.prepareStatement("select pg_notify(?, ?)")) {
                    statement.setString(1, CHANNEL_NAME);
                    statement.setString(2, payload);
                    statement.execute();
                }
                return null;
            });
            notification.markPublished();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("채팅 알림 payload 직렬화에 실패했습니다.", exception);
        }
    }

    private boolean isPostgres() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getDatabaseProductName().toLowerCase().contains("postgresql");
        } catch (SQLException exception) {
            log.warn("Cannot check database product name for chat notification publish.", exception);
            return false;
        }
    }

    private Map<String, Object> toPayload(ChatNotification notification) {
        return Map.of(
                "notificationId", notification.getId(),
                "messageId", notification.getChatMessage().getId(),
                "receiverMemberId", notification.getReceiver().getId(),
                "payload", notification.getPayload()
        );
    }
}
