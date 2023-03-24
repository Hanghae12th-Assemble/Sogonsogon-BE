package com.sparta.sogonsogon.member.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberInfoDto {

    private Long id;

    @NotBlank
    private String nickname;

    @NotBlank
    private String email;

    private String profileImageUrl;



}
