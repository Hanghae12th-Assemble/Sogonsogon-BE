package com.sparta.sogonsogon.follow.dto;

import com.sparta.sogonsogon.follow.entity.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private String followername;
    private String followingname;

    FollowResponseDto (Follow follow) {
        this.followername = follow.getFollower().getMembername();
        this.followingname = follow.getFollowing().getMembername();

    }

    public static FollowResponseDto of (Follow follow){
        return new FollowResponseDto(follow);
    }

}
