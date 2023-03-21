package com.sparta.sogonsogon.radio.dto;

import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.TimeStamped;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RadioResponseDto extends TimeStamped {
    private Long id;

    private String title; // 라디오 제목

    private String introduction; // 라디오 관련 간단한 설명

    private String backgroundImageUrl; //배경화면
    private Long memberId;
    private String nickname;

    private String membername;

    private CategoryType categoryType;

    private LocalDateTime startTime;  // 방송시작시간

    private LocalDateTime endTime;  // 방송종료 시간

    private LocalDateTime createdAt; // 생성시간

    public RadioResponseDto(Radio radio) {
        this.id = radio.getId();
        this.title = radio.getTitle();
        this.introduction = radio.getIntroduction();
        this.backgroundImageUrl = radio.getBackgroundImageUrl();
        this.nickname = radio.getMember().getNickname();
        this.membername = radio.getMember().getMembername();
//        this.startTime = radio.getStartTime();
//        this.endTime = radio.getEndTime();
        this.createdAt = radio.getCreatedAt();
        this.memberId = radio.getMember().getId();
        this.categoryType = radio.getCategoryType();
    }
}
