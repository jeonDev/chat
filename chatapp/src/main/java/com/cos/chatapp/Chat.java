package com.cos.chatapp;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;  // Mongo DB -> 자동으로 생성 Bson 타입 (but String으로)
    private String msg;
    private String sender;  // 보내는사람
    private String receiver;    // 받는사람

    private LocalDate createAt;
}
