package com.sparta.sogonsogon.member.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class SignUpRequestDto {

    @Pattern(regexp = "(?=.*?[a-z])(?=.*?[\\d]).{4,10}")
    private String membername;
    @Pattern(regexp = "(?=.*?[a-zA-Z])(?=.*?[\\d])(?=.*?[~!@#$%^&*()_+=\\-`]).{8,15}")
    private String password;
    @Email
    private String email;
    @NotNull
    private String nickname;
}
