package com.jh.chat.member.domain.entity;

import com.jh.chat.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "MEMBER",
        uniqueConstraints = @UniqueConstraint(name = "UK_MEMBER_LOGIN_ID", columnNames = "LOGIN_ID")
)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOGIN_ID", length = 30, nullable = false)
    private String loginId;

    @Column(name = "PASSWORD", length = 200, nullable = false)
    private String password;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "PHONE", length = 100)
    private String phone;

    public static Member of(String loginId,
                            String password,
                            String name
    ) {
        return of(loginId, password, name, null);
    }

    public static Member of(String loginId,
                            String password,
                            String name,
                            String phone
    ) {
        return new Member(
                null,
                loginId,
                password,
                name,
                phone
        );
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
