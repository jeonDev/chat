package com.jh.chat.friend.domain.entity;

import com.jh.chat.common.entity.BaseEntity;
import com.jh.chat.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "FRIEND",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_FRIEND_REQUESTER_TARGET",
                        columnNames = {"REQUESTER_MEMBER_ID", "FRIEND_MEMBER_ID"}
                )
        }
)
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_MEMBER_ID", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRIEND_MEMBER_ID", nullable = false)
    private Member friend;

    public static Friend of(Member requester, Member friend) {
        return new Friend(
                null,
                requester,
                friend
        );
    }
}

