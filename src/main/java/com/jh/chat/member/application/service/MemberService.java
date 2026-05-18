package com.jh.chat.member.application.service;

import com.jh.chat.member.application.service.request.MemberGenerateRequest;
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
}
