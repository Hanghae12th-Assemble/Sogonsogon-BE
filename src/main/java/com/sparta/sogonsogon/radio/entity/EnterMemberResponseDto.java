package com.sparta.sogonsogon.radio.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnterMemberResponseDto {

    private String nickname;
    private String profileImageUrl;

    public EnterMemberResponseDto(EnterMember enterMember) {
        this.nickname = enterMember.getMember().getNickname();
        this.profileImageUrl = enterMember.getMember().getProfileImageUrl();
    }

    public static EnterMemberResponseDto of(EnterMember enterMember) {
        return new EnterMemberResponseDto(enterMember);
    }
}
