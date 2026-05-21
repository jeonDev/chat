package com.jh.chat.room.domain.repository;

import com.jh.chat.room.domain.entity.ChatRoomMember;
import com.jh.chat.room.domain.entity.ChatRoomMemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findAllByMember_IdAndStatus(Long memberId, ChatRoomMemberStatus status);

    List<ChatRoomMember> findAllByChatRoom_IdAndStatus(Long chatRoomId, ChatRoomMemberStatus status);

    List<ChatRoomMember> findAllByChatRoom_Id(Long chatRoomId);

    Optional<ChatRoomMember> findByChatRoom_IdAndMember_Id(Long chatRoomId, Long memberId);
}
