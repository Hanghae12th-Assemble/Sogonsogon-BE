package com.sparta.sogonsogon.follow.service;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
import com.sparta.sogonsogon.follow.dto.FollowResponseDto;
import com.sparta.sogonsogon.follow.entity.Follow;
import com.sparta.sogonsogon.follow.repository.FollowRepository;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public Object createFollow(String followerUsername, FollowRequestDto requestDto, Member user) throws AccessDeniedException {
        // 인증된 사용자 이름으로 사용자 정보를 DB에서 조회
        Member following = memberRepository.findByMembername(user.getMembername()).orElseThrow(
                () -> new IllegalArgumentException("로그인해주세요.")
        );

        // 팔로우할 유저를 ID로 조회
        Member follower = memberRepository.findByMembername(followerUsername).orElseThrow(
                () -> new EntityNotFoundException("유저를 조회할 수 없습니다")
        );

        //팔로우 신청한 유저와 당한 유저는 동일할 순 없음
        if (following.getId() == follower.getId()) {
            throw new AccessDeniedException("팔로우 요청이 올바르지 않습니다");
        }
        // 팔로잉(팔로우 신청한 사람), 팔로워(신청을 받은 사람)으로 팔로우 엔티티 형성
        Follow follow = (Follow) followRepository.findByFollowingAndFollower(following, follower).orElse(null);

        //save로 넣을 때는 Entity로 넣어야하기에 새로 Entity를 생성
        Follow check = new Follow(follower, following);
        if (follow == null) {
            followRepository.save(check);
        }
        // 팔로우 처리가 완료되었거나, 요청을 했는 데 팔로우 신청을 한 번 더한 경우
        // -> 팔로우 삭제
        else {
            throw new AccessDeniedException("팔로우를 추가할 수 없습니다");
        }
        return FollowResponseDto.of(new Follow(follower, following));

    }


    @Transactional
    public StatusResponseDto<FollowResponseDto> unfollow(String followerUsername, Member user) throws AccessDeniedException {
        // 인증된 사용자 이름으로 사용자 정보를 DB에서 조회
        Member following = memberRepository.findByMembername(user.getMembername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

        // 팔로우할 유저를 ID로 조회
        Member follower = memberRepository.findByMembername(followerUsername).orElseThrow(
                () -> new EntityNotFoundException("유저를 조회할 수 없습니다")
        );

        //팔로우 신청한 유저와 당한 유저는 동일할 순 없음
        if (following.getId() == follower.getId()) {
            throw new AccessDeniedException("팔로우 요청이 올바르지 않습니다");
        }
        // 팔로잉(팔로우 신청한 사람), 팔로워(신청을 받은 사람)으로 팔로우 엔티티 형성
        Follow follow = (Follow) followRepository.findByFollowingAndFollower(following, follower).orElse(null);
        //save로 넣을 때는 Entity로 넣어야하기에 새로 Entity를 생성

        if (follow.getFollower() == follower) {
            followRepository.delete(follow);
        }
        throw new AccessDeniedException("팔로우 삭제 요청이 올바르지 않습니다");
    }


    @Transactional
    public StatusResponseDto<List<FollowResponseDto>> getFollowingList(Long memberId, String username) {
//
//        List<FollowResponseDto> followingList = followRepository.findAllBy

        return null;

    }

    @Transactional
    public StatusResponseDto<List<FollowResponseDto>> getFollowersList(Long memberId) {

        return null;
    }


}
