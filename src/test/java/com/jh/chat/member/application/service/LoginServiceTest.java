package com.jh.chat.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.jh.chat.common.exception.ServiceException;
import com.jh.chat.common.security.AppSecurityProperties;
import com.jh.chat.common.security.JwtTokenProvider;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Mock
    private JpaMemberRepository jpaMemberRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private JwtTokenProvider jwtTokenProvider;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        AppSecurityProperties properties = new AppSecurityProperties();
        properties.getJwt().setSecret(SECRET);
        properties.getJwt().setExpirationMillis(3_600_000);
        jwtTokenProvider = new JwtTokenProvider(properties, new JsonMapper());
        loginService = new LoginService(jpaMemberRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void loginReturnsSignedTokenWhenPasswordMatches() {
        Member member = Member.of("jhjeon", passwordEncoder.encode("password1234"), "전종현");
        ReflectionTestUtils.setField(member, "id", 1L);
        when(jpaMemberRepository.findByLoginId("jhjeon")).thenReturn(Optional.of(member));

        var response = loginService.login("jhjeon", "password1234");

        assertTrue(jwtTokenProvider.validateToken(response.accessToken()));
        assertEquals("1", jwtTokenProvider.getSubject(response.accessToken()));
    }

    @Test
    void loginRejectsWrongPassword() {
        Member member = Member.of("jhjeon", passwordEncoder.encode("password1234"), "전종현");
        when(jpaMemberRepository.findByLoginId("jhjeon")).thenReturn(Optional.of(member));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> loginService.login("jhjeon", "wrong-password"));

        assertEquals("INVALID_LOGIN", exception.getCode());
    }

    @Test
    void loginRejectsBlankInputBeforePasswordEncoder() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> loginService.login("jhjeon", null));

        assertEquals("INVALID_LOGIN", exception.getCode());
        verifyNoInteractions(jpaMemberRepository);
    }
}
