package com.sparta.sogonsogon.radio.entity;

import com.sparta.sogonsogon.enums.CategoryType;
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
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;


    @CreatedDate
    private LocalDateTime startTime; // 방송시작시간

    private int enterCnt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;

    @OneToMany(mappedBy = "radio", cascade = CascadeType.ALL)
    private List<EnterMember> enterMemberList = new ArrayList<>();


    @Builder
    public Radio(String title, String introduction,
                 String backgroundImageUrl, Member member, CategoryType categoryType ) {

        this.title = title;
        this.introduction = introduction;
        this.backgroundImageUrl = backgroundImageUrl;
        this.member = member;
        this.categoryType = categoryType;
    }


    public void updateRadio(RadioRequestDto requestDto, String backgroundImageUrl) {
        this.title = requestDto.getTitle();
        this.introduction = requestDto.getIntroduction();
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public void enter(int cnt) {
        this.enterCnt = cnt;
    }
}
