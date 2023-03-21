package com.sparta.sogonsogon.member.oauth.dto;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class MemberSessionDto implements Serializable {

    private Long id;
    private String membername;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private MemberRoleEnum role;
    private String modifiedAt;

    //Entity -> DTO
    public MemberSessionDto(Member member){
        this.id = member.getId();
        this.membername = member.getMembername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.role = member.getRole();
        this.modifiedAt = member.getModifiedAt().toString();
    }



}
