package com.sparta.sogonsogon.radio.repository;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.radio.entity.Radio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RadioRepository extends JpaRepository<Radio, Long> {
  Optional<Radio> findByTitle(String title);
}
