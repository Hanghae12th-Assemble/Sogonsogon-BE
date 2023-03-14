package com.sparta.sogonsogon.member.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.member.dto.LoginRequestDto;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.dto.SignUpRequestDto;
import com.sparta.sogonsogon.member.service.MemberService;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    //회원 가입
    @PostMapping("/signup")
    public StatusResponseDto<String> signup(@RequestBody @Valid SignUpRequestDto requestDto) throws IllegalAccessException {
        return memberService.signup(requestDto);
    }

    //로그인
    @PostMapping("/login")
    public StatusResponseDto<MemberResponseDto> login(@RequestBody LoginRequestDto requestDto, @Parameter(hidden = true) HttpServletResponse response) throws IllegalAccessException {
        return memberService.login(requestDto, response);
    }


}
