package com.jh.chat.member.application.service;

import com.jh.chat.common.exception.ErrorType;
import com.jh.chat.common.exception.ServiceException;
import com.jh.chat.common.security.JwtTokenProvider;
import com.jh.chat.member.application.service.response.LoginResponse;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LoginService {

    private final JpaMemberRepository jpaMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginService(JpaMemberRepository jpaMemberRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider
    ) {
        this.jpaMemberRepository = jpaMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(String loginId, String password) {
        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(password)) {
            throw new ServiceException(ErrorType.INVALID_LOGIN);
        }

        Member member = jpaMemberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ServiceException(ErrorType.INVALID_LOGIN));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ServiceException(ErrorType.INVALID_LOGIN);
        }

        return new LoginResponse(jwtTokenProvider.createToken(member.getId().toString()));
    }
}
