package com.sparta.sogonsogon.radio.service;

import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.follow.dto.FollowRequestDto;
import com.sparta.sogonsogon.follow.entity.Follow;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
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
        log.info(userDetails.getUsername());
        //유저인지 확인
        Member member = memberRepository.findByMembername(userDetails.getUsername()).orElseThrow(
                () -> new InsufficientAuthenticationException(ErrorMessage.ACCESS_DENIED.getMessage()) // 401 Unauthorized
        );
        // 라디오 방 이름 중복여보 확인
        Optional<Radio> found = radioRepository.findByTitle(requestDto.getTitle());
        if (found.isPresent()) {
            throw new DuplicateKeyException(ErrorMessage.DUPLICATE_RADIO_NAME.getMessage()); // 409 Conflict
        }

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
    public Map<String, Object> findAllRadios(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable sortedPageable = PageRequest.of(page, size, sort);
        Page<Radio> radioPage = radioRepository.findAll(sortedPageable);
        List<RadioResponseDto> radioResponseDtoList = radioPage.getContent().stream().map(RadioResponseDto::new).toList();

        // 방송 길이 구하는 객체 생성
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("radioCount", radioPage.getTotalElements());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", radioResponseDtoList);
        responseBody.put("metadata", metadata);

        return responseBody;
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

    // 라디오에 들어가기
    public EnterMemberResponseDto enterRadio(Long radioId, UserDetailsImpl userDetails) {

        Radio radio = radioRepository.findById(radioId).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage())
        );

        Member member = memberRepository.findById(userDetails.getUser().getId()).orElseThrow(
            () -> new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage())
        );

        EnterMember enterMember = new EnterMember(member, radio);
        radio.enter(radio.getEnterCnt() + 1);
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
            radio.enter(radio.getEnterCnt() - 1);
        }
    }

    public List<RadioResponseDto> findByTitle(String title) {
        List<Radio> list = radioRepository.findByTitleContaining(title);
        List<RadioResponseDto> radioResponseDtos = new ArrayList<>();
        for (Radio radio : list) {
            radioResponseDtos.add(new RadioResponseDto(radio));
        }
        return radioResponseDtos;
    }

    public Map<String, Object> findByCategory(int page, int size, String sortBy, CategoryType categoryType) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable sortedPageable = PageRequest.of(page, size, sort);
        Page<Radio> radioPage = radioRepository.findAllByCategoryType(categoryType, sortedPageable);
        List<RadioResponseDto> radioResponseDtoList = radioPage.getContent().stream().map(RadioResponseDto::new).toList();
        // 방송 길이 구하는 객체 생성
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("radioCount", radioPage.getTotalElements());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", radioResponseDtoList);
        responseBody.put("metadata", metadata);

        return responseBody;
    }




    // 라디오 방송 시작 및 종료 기능 추가 **********************************

    public Radio startRadio(Long radioId, UserDetailsImpl userDetails) {
        //라디오 존재여부 확인하기
        Optional<Radio> optionalRadio = radioRepository.findById(radioId);
        //라디오 존재여부 확인하기
//            Radio radio = radioRepository.findById(radioId).orElseThrow(
//                    () -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_RADIO.getMessage())
//            );

        if (optionalRadio.isPresent()) {
            Radio radio = optionalRadio.get();
            if (!userDetails.getUser().getId().equals(radio.getMember().getId())) {
                throw new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage());
            }

            radio.setStartTime(LocalDateTime.now());
            Radio saveRadio = radioRepository.save(radio);


            // NotificationService를 통해 라디오 시작 알림을 구독한 유저들에게 알림을 보낸다.
            String message = radio.getMember().getMembername()+"님이 " + radio.getStartTime() +"에 "+ radio.getTitle() + "방송을 시작하였습니다. ";
            List<Follow> followings = followRepository.findByFollower(userDetails.getUser());
            for (Follow following : followings) {
                notificationService.send(following.getFollowing(), AlarmType.eventRadioStart, message);
            }

            return saveRadio;
        } else {
            throw new RuntimeException("Invalid broadcast ID: " + radioId);
        }
    }

    public Radio endRadio(Long radioId, UserDetailsImpl userDetails) {
        Optional<Radio> optionalRadio = radioRepository.findById(radioId);
        if (optionalRadio.isPresent()) {
            Radio radio = optionalRadio.get();

            if (!userDetails.getUser().getId().equals(radio.getMember().getId())) {
                throw new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage());
            }

            // 라디오 정보를 업데이트하고
            radio.setEndTime(LocalDateTime.now());
            Radio saveRadio = radioRepository.save(radio);

            // NotificationService를 통해 라디오 종료 알림을 구독한 유저들에게 알림을 보낸다.
            String message = userDetails.getUsername() +"님이 " + radio.getEndTime() +"에 "+ radio.getTitle() + "방송을 종료하였습니다. ";
            List<Follow> followings = followRepository.findByFollower(userDetails.getUser());
            for (Follow following : followings) {
                notificationService.send(following.getFollowing(), AlarmType.eventRadioEnd, message);
            }

            return saveRadio;

        } else {
            throw new RuntimeException("Invalid broadcast ID: " + radioId);
        }
    }

}
