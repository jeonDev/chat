package com.jh.chat.room.domain.repository;

import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findAllByMemberIdAndStatus(Long memberId, ChatRoomMemberStatus status);

    List<ChatRoomMember> findAllByChatRoomIdAndStatus(Long chatRoomId, ChatRoomMemberStatus status);

    List<ChatRoomMember> findAllByChatRoomId(Long chatRoomId);

    Optional<ChatRoomMember> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
}
