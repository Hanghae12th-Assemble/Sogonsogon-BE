package com.sparta.sogonsogon.member.dto;

import com.sparta.sogonsogon.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDto {

    private String membername;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private String email;
//    private LocalDateTime createAt;

    public MemberResponseDto(Member member){
        this.membername = member.getMembername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.introduction = member.getMemberInfo();
//        this.createAt = LocalDateTime.now();
    }

}
