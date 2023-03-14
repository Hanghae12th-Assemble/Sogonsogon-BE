package com.sparta.sogonsogon.member.repository;

import com.sparta.sogonsogon.member.entity.Member;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member , Long> {

    Optional<Member> findByMembername(String membername);
    Optional<Member> findByEmail(String email);


}
