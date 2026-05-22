package com.jh.chat.member.domain.repository;

import com.jh.chat.member.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    @Query("""
            select member
            from Member member
            where lower(member.name) like lower(concat('%', :keyword, '%'))
               or lower(member.phone) like lower(concat('%', :keyword, '%'))
            """)
    List<Member> searchByNameOrPhone(@Param("keyword") String keyword);
}
