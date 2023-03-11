package com.sparta.sogonsogon.member.dto;

import com.sparta.sogonsogon.member.entity.Member;

public class MemberResponseDto {

    private String membername;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private String email;

    public MemberResponseDto(Member member){
        this.membername = member.getMembername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.introduction = member.getMemberInfo();
    }

}
