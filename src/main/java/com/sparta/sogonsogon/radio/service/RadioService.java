package com.sparta.sogonsogon.radio.service;

import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
import com.sparta.sogonsogon.follow.repository.FollowRepository;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.noti.dto.NotificationRequestDto;
import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.noti.repository.EmitterRepository;
import com.sparta.sogonsogon.noti.repository.NotificationRepository;
import com.sparta.sogonsogon.noti.service.NotificationService;
import com.sparta.sogonsogon.noti.util.AlarmType;
import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.radio.entity.EnterMember;
import com.sparta.sogonsogon.radio.entity.EnterMemberResponseDto;
import com.sparta.sogonsogon.radio.entity.Radio;
import com.sparta.sogonsogon.radio.repository.EnterMemberRepository;
import com.sparta.sogonsogon.radio.repository.RadioRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import com.sparta.sogonsogon.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RadioService {
    private final RadioRepository radioRepository;
    private final S3Uploader s3Uploader;
    private JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final EnterMemberRepository enterMemberRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final EmitterRepository emitterRepository;


    // 라디오생성
    @Transactional
    public RadioResponseDto createRadio(RadioRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {

        //유저인지 확인
        Member member = memberRepository.findByMembername(userDetails.getUsername()).orElseThrow(
            () -> new InsufficientAuthenticationException(ErrorMessage.ACCESS_DENIED.getMessage()) // 401 Unauthorized
        );
        // 라디오 방 이름 중복여보 확인
//        if (radioRepository.findByTitle(requestDto.getTitle()) != null) {
//            throw new IllegalArgumentException("이미 존재하는 라디오 제목입니다.");
//        }
        Optional<Radio> found = radioRepository.findByTitle(requestDto.getTitle());
        if (found.isPresent()) {
            throw new DuplicateKeyException(ErrorMessage.DUPLICATE_RADIO_NAME.getMessage()); // 409 Conflict
        }

//        if (requestDto.getTitle() == null) {
//            throw new IllegalArgumentException("제목을 입력해주세요"); // 400 Bad Request
//        }
//       해당 내용은 requestDto 에서 검증할 내용이기 때문에 제거

        // 라디오 룸 배경이미지 추가
        String imageUrl = s3Uploader.uploadFiles(requestDto.getBackgroundImageUrl(), "radioImages");

        Radio radio = Radio.builder()
            .member(member)
            .title(requestDto.getTitle())
            .introduction(requestDto.getIntroduction())
            .backgroundImageUrl(imageUrl)
            .categoryType(requestDto.getCategoryType())
            .build();

        radio = radioRepository.save(radio);
        return new RadioResponseDto(radio);

    }


    // 라디오 전체 조회
    @Transactional
    public List<RadioResponseDto> findAllRadios() {
        List<Radio> list = radioRepository.findAll();
        List<RadioResponseDto> radioResponseDtos = new ArrayList<>();
        for (Radio radio : list) {
            radioResponseDtos.add(new RadioResponseDto(radio));
        }
        return radioResponseDtos;
    }

    // 선택된 라디오 조회
    @Transactional
    public RadioResponseDto findRadio(Long radioId) {
        Radio radio = radioRepository.findById(radioId).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage()));
        return new RadioResponseDto(radio);
    }

    @Transactional
    public void deleteRadio(Long radioId, Member user) {

        //라디오 존재여부 확인하기
        Radio radio = radioRepository.findById(radioId).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage())
        );

        if (!user.getId().equals(radio.getMember().getId())) {
            throw new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage());
        }
        radioRepository.deleteById(radioId);


    }

    public EnterMemberResponseDto enterRadio(Long radioId, UserDetailsImpl userDetails) {

        Radio radio = radioRepository.findById(radioId).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage())
        );

        Member member = memberRepository.findById(userDetails.getUser().getId()).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage())
        );

        EnterMember enterMember = new EnterMember(member, radio);
        radio.enter(radio.getEnterCnt()+1);
        enterMemberRepository.save(enterMember);
        return EnterMemberResponseDto.of(enterMember);
    }

    public void quitRadio(Long radioId, UserDetailsImpl userDetails) {
        Radio radio = radioRepository.findById(radioId).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage())
        );

        EnterMember enterMember = enterMemberRepository.findByRadioAndMember(radio, userDetails.getUser());
        if (enterMember == null) {
            throw new IllegalArgumentException(ErrorMessage.NOT_FOUND_ENTER_MEMBER.getMessage());
        } else {
            enterMemberRepository.delete(enterMember);
            radio.enter(radio.getEnterCnt()-1);
        }
    }

    public List<RadioResponseDto> findByTitle(String title) {
        List<Radio> list = radioRepository.findByTitleContaining(title);
        List<RadioResponseDto> radioResponseDtos = new ArrayList<>();
        for (Radio radio : list){
            radioResponseDtos.add(new RadioResponseDto(radio));
        }
        return radioResponseDtos;
    }

    public List<RadioResponseDto> findByCategory(int page, int size, String sortBy, CategoryType categoryType) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Radio> radioPage = radioRepository.findAllByCategoryType(categoryType, pageable);

        List<Radio> radioList = radioPage.getContent();
        List<RadioResponseDto> radioResponseDtos = new ArrayList<>();

        for (Radio radio : radioList) {
            radioResponseDtos.add(new RadioResponseDto(radio));
        }

        return radioResponseDtos;
    }




    // 라디오 방송 시작 및 종료 기능 추가 **********************************

//    public Radio startRadio(Long radioId) {
//        // 라디오 시작 시간 가져오기
//        Radio radio = radioRepository.findById(radioId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid radio ID"));
//
//        // 라디오 시작 알림 보내기
//        notificationService.notifyRadioStarted(radio);
//    }
//
//    public Radio endRadio(Long radioId) {
//        // 라디오 종료 시간 가져오기
//        Radio radio = radioRepository.findById(radioId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid radio ID"));
//
//        // 라디오 종료 알림 보내기
//        notificationService.notifyRadioEnded(radio);
//
//        // 라디오 종료 시간 설정하기
//        radio.setEndTime(LocalDateTime.now());
//        radioRepository.save(radio);
//    }
    public Radio startRadio(Long radioId) {
        Optional<Radio> optionalRadio = radioRepository.findById(radioId);

        if (optionalRadio.isPresent()) {
            Radio radio = optionalRadio.get();
            LocalDateTime now = LocalDateTime.now();
            if (radio.getStartTime().isBefore(now) && radio.getEndTime().isAfter(now)) {
                // 이미 방송 중이면 아무것도 하지 않음
                return radio;
            } else if (radio.getStartTime().isAfter(now)) {
                // 방송 시작 시간이 아직 되지 않은 경우 시작 시간을 현재 시간으로 업데이트
                radio.setStartTime(now);
                radio = radioRepository.save(radio);

                // NotificationService로 알림 전송
                notificationService.notifyRadioStarted(radio);
            } else {
                // 방송 종료된 경우 null 반환
                return null;
            }
        }
            throw new IllegalArgumentException("Invalid radio ID: " + radioId);
    }

    public Radio endRadio(Long radioId) {
        Optional<Radio> optionalRadio = radioRepository.findById(radioId);

        if (optionalRadio.isPresent()) {
            Radio radio = optionalRadio.get();
            LocalDateTime now = LocalDateTime.now();
            if (radio.getStartTime().isBefore(now) && radio.getEndTime().isAfter(now)) {
                // 방송 중이면 종료 시간을 현재 시간으로 업데이트
                radio.setEndTime(now);
                radioRepository.save(radio);

                // NotificationService로 알림 전송
                notificationService.notifyRadioEnded(radio);

                return radio;
            } else if (radio.getEndTime().isBefore(now)) {
                // 이미 방송 종료된 경우 아무것도 하지 않음
                return radio;
            } else {
                // 아직 방송 시작되지 않은 경우 null 반환
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid broadcast ID: " + radioId);
        }
    }



}
