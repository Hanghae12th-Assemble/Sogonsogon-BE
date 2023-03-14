//package com.sparta.sogonsogon.follow.controller;
//
//import com.sparta.sogonsogon.dto.StatusResponseDto;
//import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
//import com.sparta.sogonsogon.follow.dto.FollowResponseDto;
//import com.sparta.sogonsogon.follow.service.FollowService;
//import com.sparta.sogonsogon.security.UserDetailsImpl;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.nio.file.AccessDeniedException;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/follow")
//public class FollowController {
//    private final FollowService followService;
//
//
//    @PostMapping("/{followerUsername}")
//    @Operation(summary = "팔로우 추가", description ="팔로우 추가" )
//    public StatusResponseDto<FollowResponseDto> createFollow(@PathVariable String followerUsername,
//                                                             @RequestBody FollowRequestDto requestDto,
//                                                             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
//
////        followService.createFollow(followerUsername,requestDto,userDetails.getUsername());
//        return StatusResponseDto.success((FollowResponseDto) followService.createFollow(followerUsername,requestDto,userDetails.getUser()));
//    }
//
//
//    @DeleteMapping("/{followerUsername}")
//    @Operation(summary = "팔로우 취소", description ="팔로우 쉬소" )
//    public StatusResponseDto<String> unFollow(@PathVariable String followerUsername,
//                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
//        followService.unfollow(followerUsername,userDetails.getUser());
//        return StatusResponseDto.success("팔로우 취소되었습니다.");
//    }
//
//    @GetMapping("/{memberId}/following")
//    @Operation(summary = "유저가 팔로우하는 모든 유저 가져오기", description ="팔로우하는 모든 유저 가져오기" )
//    public StatusResponseDto<List<FollowResponseDto>> getFollowingList(@PathVariable Long memberId,
//                                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
//        return followService.getFollowingList(memberId, userDetails.getUsername());
//    }
//
//    @GetMapping("/{memberId}/follower")
//    @Operation(summary = "유저를 팔로우하는 모든 사용자 가져오기", description ="유저를 팔로우하는 모든 사용자 가져오기" )
//    public StatusResponseDto<List<FollowResponseDto>> getFollowersList(@PathVariable Long memberId,
//                                                                       @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
//        return followService.getFollowersList(memberId);
//    }
//}
