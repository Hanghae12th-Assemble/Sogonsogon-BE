package com.sparta.sogonsogon.radio.dto;


import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RadioRequestDto {


    private String title; // 라디오 제목

    private String introduction; // 라디오 관련 간단한 설명

    private MultipartFile backgroundImageUrl; //배경화면


}
