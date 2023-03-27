package com.sparta.sogonsogon.radio.entity;

import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.TimeStamped;

import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "radio")
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


    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime startTime; // 방송시작시간

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime endTime ; // 방송 종료시간

    @OneToMany(mappedBy = "radio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    private int enterCnt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id")
    private Member member;


    @OneToMany(mappedBy = "radio", cascade = CascadeType.ALL)
    private List<EnterMember> enterMemberList = new ArrayList<>();



    @Builder
    public Radio(String title, String introduction,
                 String backgroundImageUrl, Member member, CategoryType categoryType) {

        this.title = title;
        this.introduction = introduction;
        this.backgroundImageUrl = backgroundImageUrl;
        this.member = member;
        this.categoryType = categoryType;
    }

//    public Radio(String title, String introduction, LocalDateTime startTime, LocalDateTime endTime) {
//        this.title = title;
//        this.introduction = introduction;
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }


    public void updateRadio(RadioRequestDto requestDto, String backgroundImageUrl) {
        this.title = requestDto.getTitle();
        this.introduction = requestDto.getIntroduction();
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public void enter(int cnt) {
        this.enterCnt = cnt;
    }


    
    //라디오 방송 시작 및 종료시 사용

    public void radioStart(Member member,String title, LocalDateTime startTime) {
        this.member = member;
        this.title = title;
        this.startTime = LocalDateTime.now();

    }
//    public void radioEnd(Member member,String title,  LocalDateTime endTime) {
//        this.member = member;
//        this.title = title;
//        this.endTime = LocalDateTime.now();
//    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // 현재 방송이 진행 중인지를 나타내는 boolean 값을 반환
    public boolean isLive() {
        LocalDateTime now = LocalDateTime.now();
        return startTime.isBefore(now) && endTime.isAfter(now);
    }



}
