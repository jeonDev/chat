package com.jh.chat.friend.application.service;

import com.jh.chat.common.exception.NotFoundException;
import com.jh.chat.friend.application.service.response.FriendMemberResult;
import com.jh.chat.friend.application.service.response.FriendSearchResult;
import com.jh.chat.friend.domain.entity.Friend;
import com.jh.chat.friend.domain.repository.JpaFriendRepository;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {

    private final JpaFriendRepository friendRepository;
    private final JpaMemberRepository memberRepository;

    public FriendService(JpaFriendRepository friendRepository, JpaMemberRepository memberRepository) {
        this.friendRepository = friendRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<FriendSearchResult> search(Long memberId, String keyword) {
        validateMemberId(memberId);
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어는 필수입니다.");
        }
        getMember(memberId);

        return memberRepository.searchByNameOrPhone(keyword.trim())
                .stream()
                .filter(member -> !member.getId().equals(memberId))
                .map(member -> FriendSearchResult.of(
                        member,
                        friendRepository.existsByRequesterIdAndFriendId(memberId, member.getId())
                ))
                .toList();
    }

    @Transactional
    public FriendMemberResult add(Long requesterMemberId, Long friendMemberId) {
        validateMemberId(requesterMemberId);
        validateMemberId(friendMemberId);
        if (requesterMemberId.equals(friendMemberId)) {
            throw new IllegalArgumentException("자기 자신은 친구로 추가할 수 없습니다.");
        }

        Member requester = getMember(requesterMemberId);
        Member friendMember = getMember(friendMemberId);

        Friend friend = friendRepository.findByRequesterIdAndFriendId(requesterMemberId, friendMemberId)
                .orElseGet(() -> friendRepository.save(Friend.of(requester, friendMember)));

        return FriendMemberResult.friendOf(friend);
    }

    @Transactional(readOnly = true)
    public List<FriendMemberResult> getMyFriends(Long memberId) {
        validateMemberId(memberId);
        getMember(memberId);

        return friendRepository.findAllByRequesterIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(FriendMemberResult::friendOf)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendMemberResult> getMembersWhoAddedMe(Long memberId) {
        validateMemberId(memberId);
        getMember(memberId);

        return friendRepository.findAllByFriendIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(FriendMemberResult::requesterOf)
                .toList();
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. memberId=%d".formatted(memberId)));
    }
}
