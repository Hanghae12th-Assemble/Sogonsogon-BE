package com.sparta.sogonsogon.member.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverMemberInfoDto {

    private  String membername;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private String naverId;
    private String accessToken;


    @Builder
    public NaverMemberInfoDto(String naverId, String nickname, String profileImageUrl, String email, String accessToken){
        this.membername = naverId;
        this.naverId = naverId;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.accessToken = accessToken;
    }

}
