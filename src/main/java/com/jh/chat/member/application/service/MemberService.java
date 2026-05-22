package com.jh.chat.member.application.service;

import com.jh.chat.common.exception.NotFoundException;
import com.jh.chat.member.application.service.request.MemberProfileUpdateRequest;
import com.jh.chat.member.application.service.request.MemberGenerateRequest;
import com.jh.chat.member.application.service.response.MemberProfileResult;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final JpaMemberRepository jpaMemberRepository;

    public MemberService(JpaMemberRepository jpaMemberRepository) {
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Transactional
    public Member generate(MemberGenerateRequest request) {
        String encPassword = request.password(); // TODO:

        return jpaMemberRepository.save(request.toEntity(encPassword));
    }

    @Transactional(readOnly = true)
    public MemberProfileResult getProfile(Long memberId) {
        return MemberProfileResult.of(getMember(memberId));
    }

    @Transactional
    public MemberProfileResult updateProfile(MemberProfileUpdateRequest request) {
        if (request.memberId() == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        Member member = getMember(request.memberId());
        member.updateProfile(request.name(), request.phone());
        return MemberProfileResult.of(member);
    }

    private Member getMember(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 필수입니다.");
        }

        return jpaMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. memberId=%d".formatted(memberId)));
    }
}
