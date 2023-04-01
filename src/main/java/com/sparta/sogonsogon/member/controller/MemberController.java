package com.sparta.sogonsogon.member.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.member.dto.*;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.service.MemberService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import com.sparta.sogonsogon.util.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final S3Uploader s3Uploader;

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
    @PutMapping(value = "/update/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 정보 수정", description = "프로필에서 보이는 회원 정보 수정")
    public StatusResponseDto<MemberResponseDto> updateMemberInfo(@PathVariable Long userId,
                                                                @RequestParam(value = "nickname") String nickname,
                                                                @RequestParam(value = "memberInfo") String memberInfo,
                                                                @RequestParam(value = "profileImageUrl") MultipartFile profileImage,
                                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        String profileImageUrl = s3Uploader.uploadFiles(profileImage, "profileImages");
        MemberRequestDto requestDto = new MemberRequestDto(nickname, memberInfo, profileImageUrl);
        return StatusResponseDto.success(HttpStatus.OK, memberService.update(userId, requestDto, userDetails));
    }

    //해당 고유 아이디 조회
    @GetMapping("/")
    @Operation(summary = "고유 아이디 조회", description = "고유 아이디로 사용자 조회")
    public StatusResponseDto<MemberResponseDto> findbyMembername(@RequestParam(value = "membername") String membername) {
        return StatusResponseDto.success(HttpStatus.OK, memberService.getInfoByMembername(membername));
    }

    //해당 유저 닉네임으로 조회
    @GetMapping("/nickname")
    @Operation(summary = "닉네임 조회", description = "단어로 해당 닉네임에 포함되는 모든 사용자 조회")
    public StatusResponseDto<List<MemberOneResponseDto>> findListByNickname(@RequestParam(value = "nickname") String nickname){
        return memberService.getListByNickname(nickname);
    }

    @GetMapping("/similar-nickname")
    @Operation(summary = "유사한 닉네임으로 유저 조회", description = "유사한 닉네임 조회 무한스크롤 적용")
    public ResponseEntity<List<MemberOneResponseDto>> getMembersBySimilarNickname(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                    @RequestParam(value = "size",defaultValue = "10") int size,
                                                                    @RequestParam(value = "sortBy",defaultValue = "id") String sortBy,
                                                                    @RequestParam(value = "nickname") String nickname) {
        return ResponseEntity.ok(memberService.getListBySimilarNickname(page, size, sortBy, nickname));

    }

    //상세 사용자 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "상세 회원 조회", description = "id 로 조회 하여 해당 회원의 정보를 가져옴")
    public StatusResponseDto<MemberResponseDto> detailsMember(@PathVariable Long memberId){
        return memberService.detailsMember(memberId);
    }

}
