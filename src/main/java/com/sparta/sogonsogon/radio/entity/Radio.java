package com.sparta.sogonsogon.radio.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Radio extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 라디오 제목

    @Column(nullable = false)
    private String introduction; // 라디오 관련 간단한 설명

    @Column
    @Nullable
    private String backgroundImageUrl; //배경화면
//
//    @Column(nullable = false)
//    private LocalDateTime start_Time; // 방송시작시간
//
//    @Column
//    @Nullable
//    private LocalDateTime end_Time; // 방송종료 시간
//
//    @Column(nullable = false)
//    private LocalDateTime created_At; // 생성시간

    @ManyToMany(fetch =FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
