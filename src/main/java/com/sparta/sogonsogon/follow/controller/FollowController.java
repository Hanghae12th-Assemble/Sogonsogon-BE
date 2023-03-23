package com.sparta.sogonsogon.follow.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
import com.sparta.sogonsogon.follow.dto.FollowResponseDto;
import com.sparta.sogonsogon.follow.service.FollowService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{memberId}")
    @Operation(summary = "팔로우 토글", description = "팔로우 토글")
    public StatusResponseDto<FollowResponseDto> follow(@PathVariable Long memberId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 팔로우를 토글로 구현, 홀수번 팔로우, 짝수번 언팔로우
        return StatusResponseDto.success(HttpStatus.OK, followService.toggleFollow(memberId, userDetails));
    }


    @GetMapping("/{memberId}/following")
    @Operation(summary = "유저를 팔로잉하는 모든 사용자 가져오기", description ="유저를 팔로잉하는 모든 사용자 가져오기" )
    public StatusResponseDto<List<FollowResponseDto>> getFollowings(@PathVariable Long memberId){
        return StatusResponseDto.success(HttpStatus.OK,followService.getFollowings(memberId)) ;
    }

    @GetMapping("/{memberId}/follower")
    @Operation(summary = "유저가 팔로워하는 모든 유저 가져오기", description ="유저가 팔로워하는 모든 유저 가져오기" )
    public StatusResponseDto<List<FollowResponseDto>> getFollowers(@PathVariable Long memberId){
        return StatusResponseDto.success(HttpStatus.OK,followService.getFollowers(memberId));
    }
}
