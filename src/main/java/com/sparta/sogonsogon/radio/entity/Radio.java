package com.sparta.sogonsogon.radio.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.TimeStamped;

import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Radio extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 라디오 제목

    @Column(nullable = false)
    private String introduction; // 라디오 관련 간단한 설명

    @Column
    private String backgroundImageUrl; //배경화면
    @CreatedDate
    private LocalDateTime createdAt; // 방 생성시간

    @CreatedDate
    private LocalDateTime startTime; // 방송시작시간

    @LastModifiedDate
    private LocalDateTime endTime; // 방송종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;


    @Builder
    public Radio(String title, String introduction,
                 String backgroundImageUrl, Member member ) {

        this.title = title;
        this.introduction = introduction;
        this.backgroundImageUrl = backgroundImageUrl;
        this.member = member;
    }


    public void updateRadio(RadioRequestDto requestDto, String backgroundImageUrl) {
        this.title = requestDto.getTitle();
        this.introduction = requestDto.getIntroduction();
        this.backgroundImageUrl = backgroundImageUrl;
    }
}
