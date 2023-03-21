package com.sparta.sogonsogon.radio.service;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RadioService {
    private final RadioRepository radioRepository;
    private final S3Uploader s3Uploader;
    private JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final EnterMemberRepository enterMemberRepository;


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
}
