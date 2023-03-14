package com.sparta.sogonsogon.follow.dto;

import com.sparta.sogonsogon.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class FollowRequestDto {

    private Member follower;

    private Member following;
//    private Member user;
//    private boolean isAccepted;

}
