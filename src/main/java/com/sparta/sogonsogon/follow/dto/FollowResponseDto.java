package com.sparta.sogonsogon.follow.dto;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.follow.entity.Follow;
import com.sparta.sogonsogon.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private Long id;
    private String followername;
    private String followingname;
//    private boolean isFollowed;

    FollowResponseDto (Follow follow) {
        this.followername = follow.getFollower().getMembername();
        this.followingname = follow.getFollowing().getMembername();

    }

    public FollowResponseDto(String followername, String followingname) {
        this.followername = followername;
        this.followingname = followingname;
    }

    public static FollowResponseDto of (Follow follow){
        return new FollowResponseDto(follow);
    }




}
