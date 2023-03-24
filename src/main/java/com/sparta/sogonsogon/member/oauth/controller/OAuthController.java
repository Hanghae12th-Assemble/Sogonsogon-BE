package com.sparta.sogonsogon.member.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.oauth.service.KakaoMemberService;
import com.sparta.sogonsogon.member.oauth.service.NaverMemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class OAuthController {

    private final KakaoMemberService kakaoMemberService;
    private final NaverMemberService naverMemberService;

    //소셜 카카오 로그인
    @ApiOperation(value = "카카오 로그인", notes = "카카오로 로그인 하기")
    @GetMapping("/login/kakao")
    public StatusResponseDto<MemberResponseDto> kakaologin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException{
        return kakaoMemberService.kakologin(code, response);
    }

    // 소셜 네이버 로그인
    @ApiOperation(value = "네이버 로그인", notes = "네이버로 로그인 또는 회원 가입 진행")
    @GetMapping("/login/naver")
    public StatusResponseDto<MemberResponseDto> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException{
        return naverMemberService.naverlogin(code, state, response);
    }


}
