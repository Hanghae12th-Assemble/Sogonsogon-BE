//package com.sparta.sogonsogon.radio.service;
//
//import com.sparta.sogonsogon.dto.StatusResponseDto;
//import com.sparta.sogonsogon.jwt.JwtUtil;
//import com.sparta.sogonsogon.member.entity.Member;
//import com.sparta.sogonsogon.member.repository.MemberRepository;
//import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
//import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
//import com.sparta.sogonsogon.radio.entity.Radio;
//import com.sparta.sogonsogon.radio.repository.RadioRepository;
//import com.sparta.sogonsogon.security.UserDetailsImpl;
//import com.sparta.sogonsogon.util.S3Uploader;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//
//@Service
//@RequiredArgsConstructor
//public class RadioService {
//    private final RadioRepository radioRepository;
//    private final S3Uploader s3Uploader;
//    private JwtUtil jwtUtil;
//    private final MemberRepository memberRepository;
//
//
//    // 라디오생성
//    @Transactional
//    public StatusResponseDto<RadioResponseDto> createRadio(RadioRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
//
//        if (userDetails == null) {
//            throw new IllegalArgumentException("비어있음");
//        }
//
//        //유저인지 확인
//        Member member = memberRepository.findByMembername(userDetails.getUsername()).orElseThrow(
//            () -> new IllegalArgumentException("로그인해주세요")
//        );
//        // 라디오 방 이름 중복여보 확인
////        if (radioRepository.findByTitle(requestDto.getTitle()) != null) {
////            throw new IllegalArgumentException("이미 존재하는 라디오 제목입니다.");
////        }
//        Optional<Radio> found = radioRepository.findByTitle(requestDto.getTitle());
//        if (found.isPresent()) {
//            throw new IllegalArgumentException("이미 존재하는 라디오 제목입니다.");
//        }
//
//        if (requestDto.getTitle() == null) {
//            throw new IllegalArgumentException("제목을 입력해주세요");
//        }
//
//        // 라디오 룸 배경이미지 추가
//        String imageUrl = s3Uploader.upload(requestDto.getBackgroundImageUrl());
//
//        Radio radio = Radio.builder()
//            .member(member)
//            .title(requestDto.getTitle())
//            .introduction(requestDto.getIntroduction())
//            .backgroundImageUrl(imageUrl)
//            .build();
//
//        radio = radioRepository.save(radio);
//        return StatusResponseDto.success(new RadioResponseDto(radio));
//
//    }
//
//
//    // 라디오 전체 조회
//    @Transactional
//    public StatusResponseDto<List<RadioResponseDto>> findAllRadios() {
//        List<Radio> list = radioRepository.findAll();
//        List<RadioResponseDto> radioResponseDtos = new ArrayList<>();
//        for (Radio radio : list) {
//            radioResponseDtos.add(new RadioResponseDto(radio));
//        }
//        return StatusResponseDto.success(radioResponseDtos);
//    }
//
//    // 선택된 라디오 조회
//    @Transactional
//    public StatusResponseDto<RadioResponseDto> findRadio(Long radidId) {
//        Radio radio = radioRepository.findById(radidId).orElseThrow(
//            () -> new IllegalArgumentException("조회된 라디오가 없습니다."));
//        return StatusResponseDto.success(new RadioResponseDto(radio));
//    }
//
//    // 선택된 라디오 정보 조회
//    @Transactional
//    public StatusResponseDto<RadioResponseDto> updateRadio(Long radidId, RadioRequestDto requestDto, String username) throws IOException {
//
//        // 라디오 방 유저인지 확인
//        Member membername = memberRepository.findByMembername(username).orElseThrow(
//            () -> new IllegalArgumentException("라디오 방 주인이 아닙니다")
//        );
//
//        //라디오 존재여부 확인하기
//        Radio radio = radioRepository.findById(radidId).orElseThrow(
//            () -> new IllegalArgumentException("라디오가 존재하지 않습니다.")
//        );
//
//        String newBGIamgeUrl = null;
//        if (requestDto.getBackgroundImageUrl() != null) {
//            // 새로운 이미지 생성
//            newBGIamgeUrl = s3Uploader.upload(requestDto.getBackgroundImageUrl());
//        } else {
//            //기존이미지 유지
//            newBGIamgeUrl = radio.getBackgroundImageUrl();
//        }
//        radio.updateRadio(requestDto, newBGIamgeUrl);
//        return StatusResponseDto.success(new RadioResponseDto(radio));
//    }
//
//
//    @Transactional
//    public StatusResponseDto<RadioResponseDto> deleteRadio(Long radioId, Member user) {
//
//        //라디오 존재여부 확인하기
//        Radio radio = radioRepository.findById(radioId).orElseThrow(
//            () -> new IllegalArgumentException("라디오가 존재하지 않습니다.")
//        );
//
//        if (user.getMembername().equals(radio.getMember().getMembername())) {
//            radioRepository.deleteById(radioId);
//        }
//        throw new IllegalArgumentException("다른 사용자가 생성된 라디오는 삭제할 수 없습니다.");
//    }
//}
