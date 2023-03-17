package com.sparta.sogonsogon.member.dto;

import com.sparta.sogonsogon.member.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDto {

    private Long id;
    private String membername;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private String email;
    private Boolean isFollowCheck;


    public MemberResponseDto(Member member){
        this.id = member.getId();
        this.membername = member.getMembername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.introduction = member.getMemberInfo();
        if (member.getFollowing().isEmpty()){
            this.isFollowCheck = false;
        }else {
            this.isFollowCheck = true;
        }
    }



}
