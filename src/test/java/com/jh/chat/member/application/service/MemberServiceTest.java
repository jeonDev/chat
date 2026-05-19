package com.jh.chat.member.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.jh.chat.common.exception.ServiceException;
import com.jh.chat.member.application.service.request.MemberGenerateRequest;
import com.jh.chat.member.domain.entity.Member;
import com.jh.chat.member.domain.repository.JpaMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private JpaMemberRepository jpaMemberRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(jpaMemberRepository, passwordEncoder);
    }

    @Test
    void generateStoresEncodedPassword() {
        when(jpaMemberRepository.existsByLoginId("jhjeon")).thenReturn(false);
        when(jpaMemberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member member = memberService.generate(new MemberGenerateRequest("jhjeon", "password1234", "전종현"));

        assertNotEquals("password1234", member.getPassword());
        assertTrue(passwordEncoder.matches("password1234", member.getPassword()));
    }

    @Test
    void generateRejectsBlankInput() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> memberService.generate(new MemberGenerateRequest("jhjeon", " ", "전종현")));

        assertEquals("INVALID_REQUEST", exception.getCode());
        verifyNoMoreInteractions(jpaMemberRepository);
    }

    @Test
    void generateRejectsPasswordLongerThanBcryptLimit() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> memberService.generate(new MemberGenerateRequest("jhjeon", "a".repeat(73), "전종현")));

        assertEquals("INVALID_REQUEST", exception.getCode());
        verifyNoMoreInteractions(jpaMemberRepository);
    }

    @Test
    void generateRejectsAlreadyJoinedLoginId() {
        when(jpaMemberRepository.existsByLoginId("jhjeon")).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> memberService.generate(new MemberGenerateRequest("jhjeon", "password1234", "전종현")));

        assertEquals("ALREADY_JOINED_MEMBER", exception.getCode());
        verify(jpaMemberRepository).existsByLoginId("jhjeon");
        verifyNoMoreInteractions(jpaMemberRepository);
    }

    @Test
    void generateMapsUniqueConstraintViolationToAlreadyJoinedMember() {
        when(jpaMemberRepository.existsByLoginId("jhjeon")).thenReturn(false);
        when(jpaMemberRepository.save(any(Member.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> memberService.generate(new MemberGenerateRequest("jhjeon", "password1234", "전종현")));

        assertEquals("ALREADY_JOINED_MEMBER", exception.getCode());
    }
}
