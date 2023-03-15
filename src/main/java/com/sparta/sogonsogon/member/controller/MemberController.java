package com.sparta.sogonsogon.member.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.member.dto.*;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.service.MemberService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    //회원 가입
    @PostMapping("/signup")
    public StatusResponseDto<MemberResponseDto> signup(@RequestBody @Valid SignUpRequestDto requestDto) throws IllegalAccessException {
        return StatusResponseDto.success(HttpStatus.CREATED, memberService.signup(requestDto));
    }

    //로그인
    @PostMapping("/login")
    public StatusResponseDto<MemberResponseDto> login(@RequestBody LoginRequestDto requestDto, @Parameter(hidden = true) HttpServletResponse response) {
        return StatusResponseDto.success(HttpStatus.OK, memberService.login(requestDto, response));
    }

    //회원 정보 수정
    @ResponseBody
    @PutMapping("/update/{userId}")
    public StatusResponseDto<MemberResponseDto> updateMemberInfo(@PathVariable Long userId,
                                                                @RequestParam(value = "nickname") String nickname,
                                                                @RequestParam(value = "password") String password,
                                                                @RequestParam(value = "memberInfo") String memberInfo,
                                                                @RequestParam(value = "profileImage")MultipartFile multipartFile,
                                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        MemberRequestDto memberRequestDto = new MemberRequestDto(nickname, password, memberInfo, multipartFile);
        return memberService.update(userId, memberRequestDto, userDetails);
    }

    //해당 고유 아이디 조회
    @GetMapping("/")
    public StatusResponseDto<Optional<Member>> findbyMembername(@RequestBody String membername){
        return memberService.getInfoByMembername(membername);
    }

    //해당 유저 닉네임으로 조회
    @GetMapping("/nickname")
    public StatusResponseDto<List<MemberOneResponseDto>> findListByNickname(@RequestBody String nickname){
        return memberService.getListByNickname(nickname);
    }

}
