package com.sparta.sogonsogon.follow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
import com.sparta.sogonsogon.member.entity.Member;

import com.sparta.sogonsogon.noti.entity.TimeStamped;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity(name = "follow")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Follow extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Member following;

//    public Follow(Member follower, Member following) {
//        this.follower = follower;
//        this.following = following;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "radio_id")
//    private Radio radio;



    public Follow(FollowRequestDto followRequestDto) {
        this.follower = followRequestDto.getFollower();
        this.following = followRequestDto.getFollowing();
    }


}


