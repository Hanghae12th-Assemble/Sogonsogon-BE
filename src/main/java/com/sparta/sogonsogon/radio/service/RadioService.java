package com.sparta.sogonsogon.radio.service;

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
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


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
}
