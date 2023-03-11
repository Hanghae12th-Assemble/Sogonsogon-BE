package com.sparta.sogonsogon.radio.service;

import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.radio.dto.StatusResponseDto;
import com.sparta.sogonsogon.radio.repository.RadioRepository;
import com.sparta.sogonsogon.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RadioService {
    private final RadioRepository radioRepository;
    private final S3Uploader s3Uploader;
    private JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Transactional
    public StatusResponseDto<RadioResponseDto> createRadio(RadioRequestDto requestDto, String membername) {
        //유저인지 확인
        Member member = memberRepository.findByMembername(membername).orElseThrow(
                ()-> new IllegalArgumentException("로그인해주세요")
        );
        try{
            String imageUrl = s3Uploader.upload(requestDto.getBackgroundImageUrl());

        }catch (IOException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}
