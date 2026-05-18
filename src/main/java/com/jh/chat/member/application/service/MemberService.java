package com.jh.chat.member.application.service;

import com.jh.chat.common.exception.ErrorType;
import com.jh.chat.common.exception.ServiceException;
import com.jh.chat.member.application.service.request.MemberGenerateRequest;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final JpaMemberRepository jpaMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(JpaMemberRepository jpaMemberRepository,
                         PasswordEncoder passwordEncoder
    ) {
        this.jpaMemberRepository = jpaMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Member generate(MemberGenerateRequest request) {
        if (jpaMemberRepository.existsByLoginId(request.loginId())) {
            throw new ServiceException(ErrorType.ALREADY_JOINED_MEMBER);
        }

        String encPassword = passwordEncoder.encode(request.password());

        return jpaMemberRepository.save(request.toEntity(encPassword));
    }
}
