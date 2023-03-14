package com.sparta.sogonsogon.member.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
public class LoginRequestDto {
    @Email
    @NotNull(message = "이메일을 입력해주세요.")
    private String email;
    @NotNull(message = "비밀번호 길이는 8자 이상, 15자 이하입니다.")
    private String password;
}
