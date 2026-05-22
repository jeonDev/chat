package com.jh.chat.friend.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.chat.friend.application.service.response.FriendSearchResult;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private JpaMemberRepository memberRepository;

    @Test
    void search_findsMembersByNameOrPhone() {
        Member me = saveMember("friend-me", "Me", "010-0000-0000");
        Member nameMatched = saveMember("friend-name", "Alice", "010-1111-1111");
        Member phoneMatched = saveMember("friend-phone", "Bob", "010-2222-3333");

        List<FriendSearchResult> nameResults = friendService.search(me.getId(), "Ali");
        List<FriendSearchResult> phoneResults = friendService.search(me.getId(), "3333");

        assertThat(nameResults)
                .extracting(FriendSearchResult::memberId)
                .containsExactly(nameMatched.getId());
        assertThat(phoneResults)
                .extracting(FriendSearchResult::memberId)
                .containsExactly(phoneMatched.getId());
    }

    @Test
    void add_addsFriendOnlyToRequesterAndShowsReverseList() {
        Member me = saveMember("friend-requester", "Requester", "010-1234-0000");
        Member target = saveMember("friend-target", "Target", "010-5678-0000");

        friendService.add(me.getId(), target.getId());

        assertThat(friendService.getMyFriends(me.getId()))
                .extracting(friend -> friend.memberId())
                .containsExactly(target.getId());
        assertThat(friendService.getMyFriends(target.getId())).isEmpty();
        assertThat(friendService.getMembersWhoAddedMe(target.getId()))
                .extracting(friend -> friend.memberId())
                .containsExactly(me.getId());
        assertThat(friendService.search(me.getId(), "Target"))
                .extracting(FriendSearchResult::alreadyFriend)
                .containsExactly(true);
    }

    private Member saveMember(String loginId, String name, String phone) {
        return memberRepository.save(Member.of(loginId, "password", name, phone));
    }
}

