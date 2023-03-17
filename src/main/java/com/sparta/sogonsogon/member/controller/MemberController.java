package com.sparta.sogonsogon.member.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.member.dto.*;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.service.MemberService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "회원가입", description = "회원 가입 기능 ")
    public StatusResponseDto<MemberResponseDto> signup(@RequestBody @Valid SignUpRequestDto requestDto) throws IllegalAccessException {
        return StatusResponseDto.success(HttpStatus.CREATED, memberService.signup(requestDto));
    }

    //로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 기능")
    public StatusResponseDto<MemberResponseDto> login(@RequestBody LoginRequestDto requestDto, @Parameter(hidden = true) HttpServletResponse response) {
        return StatusResponseDto.success(HttpStatus.OK, memberService.login(requestDto, response));
    }

    //회원 정보 수정
    @ResponseBody
    @PutMapping("/update/{userId}")
    @Operation(summary = "회원 정보 수정", description = "프로필에서 보이는 회원 정보 수정")
    public StatusResponseDto<MemberResponseDto> updateMemberInfo(@PathVariable Long userId,
                                                                @RequestParam(value = "nickname") String nickname,
                                                                @RequestParam(value = "memberInfo") String memberInfo,
                                                                @RequestParam(value = "profileImage")MultipartFile multipartFile,
                                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        MemberRequestDto memberRequestDto = new MemberRequestDto(nickname, memberInfo, multipartFile);
        return memberService.update(userId, memberRequestDto, userDetails);
    }

    //해당 고유 아이디 조회
    @GetMapping("/")
    @Operation(summary = "고유 아이디 조회", description = "고유 아이디로 사용자 조회")
    public StatusResponseDto<List<MemberResponseDto>> findbyMembername(@RequestParam(value = "membername") String membername){
        return memberService.getInfoByMembername(membername);
    }

    //해당 유저 닉네임으로 조회
    @GetMapping("/nickname")
    @Operation(summary = "닉네임 조회", description = "단어로 해당 닉네임에 포함되는 모든 사용자 조회")
    public StatusResponseDto<List<MemberOneResponseDto>> findListByNickname(@RequestParam(value = "nickname") String nickname){
        return memberService.getListByNickname(nickname);
    }

}
