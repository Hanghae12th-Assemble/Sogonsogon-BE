package com.sparta.sogonsogon.radio.dto;

import com.sparta.sogonsogon.radio.entity.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RadioResponseDto extends Timestamped {
    private Long id;

    private String title; // 라디오 제목

    private String introduction; // 라디오 관련 간단한 설명

    private String backgroundImageUrl; //배경화면

    private LocalDateTime start_Time;  // 방송시작시간

    private LocalDateTime end_Time;  // 방송종료 시간

    private LocalDateTime created_At; // 생성시간

}
