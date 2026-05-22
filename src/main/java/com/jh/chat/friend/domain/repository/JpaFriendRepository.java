package com.jh.chat.friend.domain.repository;

import com.jh.chat.friend.domain.entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByRequesterIdAndFriendId(Long requesterMemberId, Long friendMemberId);

    Optional<Friend> findByRequesterIdAndFriendId(Long requesterMemberId, Long friendMemberId);

    List<Friend> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterMemberId);

    List<Friend> findAllByFriendIdOrderByCreatedAtDesc(Long friendMemberId);
}
