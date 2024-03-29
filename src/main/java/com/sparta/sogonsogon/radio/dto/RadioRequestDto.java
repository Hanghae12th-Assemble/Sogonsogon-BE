package com.sparta.sogonsogon.radio.dto;


import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RadioRequestDto {


    @NotBlank
    private String title; // 라디오 제목

    private String introduction; // 라디오 관련 간단한 설명

    private MultipartFile backgroundImageUrl; //배경화면

    private CategoryType categoryType; // 카테고리


}
