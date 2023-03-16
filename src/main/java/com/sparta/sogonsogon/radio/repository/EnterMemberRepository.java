package com.sparta.sogonsogon.radio.repository;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.radio.entity.EnterMember;
import com.sparta.sogonsogon.radio.entity.Radio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterMemberRepository extends JpaRepository<EnterMember, Long> {

    EnterMember findByRadioAndMember(Radio radio, Member memner);
}
