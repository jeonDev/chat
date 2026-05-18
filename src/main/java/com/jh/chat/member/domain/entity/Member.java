package com.jh.chat.member.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOGIN_ID", length = 30)
    private String loginId;

    @Column(name = "PASSWORD", length = 200)
    private String password;

    @Column(name = "NAME", length = 100)
    private String name;

    public static Member of(String loginId,
                            String password,
                            String name
    ) {
        return new Member(
                null,
                loginId,
                password,
                name
        );
    }
}
