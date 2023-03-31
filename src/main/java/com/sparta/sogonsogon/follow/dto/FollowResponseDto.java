package com.sparta.sogonsogon.follow.dto;

import com.sparta.sogonsogon.follow.entity.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private Long id;
    private String followername;
    private String followingname;
//    private boolean isFollowed;
    private String message;
    private Boolean isFollow;

    FollowResponseDto (Follow follow, String message, Boolean isFollow) {
        this.followername = follow.getFollower().getNickname();
        this.followingname = follow.getFollowing().getNickname();
        this.message = message;
        this.isFollow = isFollow;

    }

    public FollowResponseDto(String followername, String followingname) {
        this.followername = followername;
        this.followingname = followingname;
    }

    public static FollowResponseDto of (Follow follow, String message, Boolean isFollow){
        return new FollowResponseDto(follow,message,isFollow);
    }




}
