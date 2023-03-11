package com.sparta.sogonsogon.radio.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class RadioRequestDto {

    private String title; // 라디오 제목

    private String introduction; // 라디오 관련 간단한 설명

    private MultipartFile backgroundImageUrl; //배경화면

//    private LocalDateTime start_Time;  // 방송시작시간
//
//    private LocalDateTime end_Time;  // 방송종료 시간
//
//    private LocalDateTime created_At; // 생성시간
}
