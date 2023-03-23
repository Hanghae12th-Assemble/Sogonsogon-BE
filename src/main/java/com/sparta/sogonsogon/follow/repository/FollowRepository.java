package com.sparta.sogonsogon.follow.repository;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.follow.dto.FollowResponseDto;
import com.sparta.sogonsogon.follow.entity.Follow;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.radio.entity.Radio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findByFollower(Member follower);

    List<Follow> findByFollowingId(Long followingId);

    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);

    @Query("select f.follower from follow f where f.following.id = :radioId")
    List<Member> findSubscribersByRadioId(Long radioId);
}

