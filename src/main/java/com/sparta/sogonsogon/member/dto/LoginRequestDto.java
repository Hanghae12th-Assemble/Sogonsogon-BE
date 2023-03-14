package com.sparta.sogonsogon.member.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
public class LoginRequestDto {
    @Email
    @NotNull() // BAD_REQUEST
    private String email;
    @NotNull()
    private String password;
}
