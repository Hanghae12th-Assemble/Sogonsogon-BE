package com.sparta.sogonsogon.member.dto;

import com.sparta.sogonsogon.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberOneResponseDto {

    private Long id;
    private String membername;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private String email;


    public static MemberOneResponseDto of(Member member){
        return MemberOneResponseDto.builder()
                .id(member.getId())
                .membername(member.getMembername())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImageUrl(member.getProfileImageUrl())
                .introduction(member.getMemberInfo())
                .build();
    }
}
