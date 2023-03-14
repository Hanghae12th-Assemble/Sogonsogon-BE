package com.sparta.sogonsogon.member.dto;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class SignUpRequestDto {

    // 400 BAD_REQUEST

    // 알파벳 소문자가 1글자 이상, 숫자가 1개 이상 들어가야하는 정규식
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d).+$", message = "알파벳 소문자와 숫자가 포함되어야 합니다.")
    @Size(min = 4, max = 10, message = "아이디 길이는 4자 이상, 10자 이하입니다.")
    private String membername;

    // 알파벳 소문자와 대문자 최소 1개 이상, 숫자 1개 이상, 특수문자 1개 이상이 들어가야하는 정규식
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+]).+$", message = "알파벳 소문자와 대문자, 숫자, 특수문자가 포함되어야 합니다.")
    @Size(min = 8, max = 15, message = "비밀번호 길이는 8자 이상, 15자 이하입니다.")
    private String password;
    @Email
    private String email;
    @NotBlank //  null이 아니고, 최소한 한 개 이상의 공백이 아닌 문자가 포함
    private String nickname;
}
