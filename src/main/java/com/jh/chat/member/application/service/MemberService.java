package com.jh.chat.member.application.service;

import com.jh.chat.common.exception.ErrorType;
import com.jh.chat.common.exception.NotFoundException;
import com.jh.chat.common.exception.ServiceException;
import com.jh.chat.member.application.service.request.MemberGenerateRequest;
import com.jh.chat.member.application.service.request.MemberProfileUpdateRequest;
import com.jh.chat.member.application.service.response.MemberProfileResult;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MemberService {

    private static final int MAX_LOGIN_ID_LENGTH = 30;
    private static final int MAX_BCRYPT_PASSWORD_BYTES = 72;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_PHONE_LENGTH = 100;

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
        validateGenerateRequest(request);

        if (jpaMemberRepository.existsByLoginId(request.loginId())) {
            throw new ServiceException(ErrorType.ALREADY_JOINED_MEMBER);
        }

        String encPassword = passwordEncoder.encode(request.password());

        try {
            return jpaMemberRepository.save(request.toEntity(encPassword));
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(ErrorType.ALREADY_JOINED_MEMBER);
        }
    }

    @Transactional(readOnly = true)
    public MemberProfileResult getProfile(Long memberId) {
        return MemberProfileResult.of(getMember(memberId));
    }

    @Transactional
    public MemberProfileResult updateProfile(MemberProfileUpdateRequest request) {
        if (request == null
                || request.memberId() == null
                || !StringUtils.hasText(request.name())
                || request.name().length() > MAX_NAME_LENGTH
                || phoneTooLong(request.phone())) {
            throw new ServiceException(ErrorType.INVALID_REQUEST);
        }

        Member member = getMember(request.memberId());
        member.updateProfile(request.name(), request.phone());
        return MemberProfileResult.of(member);
    }

    private void validateGenerateRequest(MemberGenerateRequest request) {
        if (request == null
                || !StringUtils.hasText(request.loginId())
                || !StringUtils.hasText(request.password())
                || !StringUtils.hasText(request.name())) {
            throw new ServiceException(ErrorType.INVALID_REQUEST);
        }
        if (request.loginId().length() > MAX_LOGIN_ID_LENGTH
                || request.password().getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_PASSWORD_BYTES
                || request.name().length() > MAX_NAME_LENGTH
                || phoneTooLong(request.phone())) {
            throw new ServiceException(ErrorType.INVALID_REQUEST);
        }
    }

    private boolean phoneTooLong(String phone) {
        return phone != null && phone.length() > MAX_PHONE_LENGTH;
    }

    private Member getMember(Long memberId) {
        if (memberId == null) {
            throw new ServiceException(ErrorType.INVALID_REQUEST);
        }

        return jpaMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. memberId=%d".formatted(memberId)));
    }
}
