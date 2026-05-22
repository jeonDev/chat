package com.jh.chat.member.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.jh.chat.member.application.service.request.MemberProfileUpdateRequest;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class MemberProfileServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JpaMemberRepository memberRepository;

    @Test
    void updateProfile_updatesNameAndPhone() {
        Member member = memberRepository.save(Member.of("profile-user", "password", "before", "010-0000-0000"));

        var result = memberService.updateProfile(
                new MemberProfileUpdateRequest(member.getId(), "after", "010-1111-2222")
        );

        assertThat(result.name()).isEqualTo("after");
        assertThat(result.phone()).isEqualTo("010-1111-2222");
        assertThat(memberService.getProfile(member.getId()).phone()).isEqualTo("010-1111-2222");
    }
}

