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


public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);

    List<Follow> findByFollowingId(Long memberId);

    List<Follow> findByFollower(Member member);

    List<Member> findByFollowing(Member member);
    List<Member> findSubscribersByRadioId(Long radioId);

    List<Radio> findFollowedRadiosByMemberId(Long memberId);


//    List<Follow> findByFollowingId(Long followingId);
//    List<Follow> findByFollowerId(Long followerId);
//
//    List<Follow> findByFollowing(Long memberId);


//    List<Follow> findByFollower(Member follower);
//
//    List<Follow> findByFollowing(Member following);
//
//    Follow findByFollowerAndFollowing(Member follower, Member following);

//    StatusResponseDto<List<FollowResponseDto>> findFollowersByFollowingId(Long memberId);
}
