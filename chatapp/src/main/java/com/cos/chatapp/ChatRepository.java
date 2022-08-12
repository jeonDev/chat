package com.cos.chatapp;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    
    @Tailable   // 커서를 안닫고 계속 유지
    @Query("{sender:?0, recever:?1")
    Flux<Chat> mFindBySender(String sender, String recevier); // Flux(흐름) : 연결성! 
}
