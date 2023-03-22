package com.sparta.sogonsogon.member.oauth.dto;

import com.mysql.cj.conf.StringProperty;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class MemberSessionDto implements Serializable {

//    private Long id;
    private String membername;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private MemberRoleEnum role;
    private String modifiedAt;

    //Entity -> DTO
    public MemberSessionDto(Member member){
//        this.id = member.getId();
        this.membername = member.getMembername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.role = member.getRole();
        this.modifiedAt = member.getModifiedAt().toString();
    }

    @Builder
    public MemberSessionDto(String membername, String nickname, String email, String profileImageUrl){
        this.membername = membername;
        this.nickname = nickname;
        this.email = email;
        this.role = MemberRoleEnum.SOCIAL;
        this.profileImageUrl = profileImageUrl;
        this.modifiedAt = LocalDateTime.now().toString();
    }


}
