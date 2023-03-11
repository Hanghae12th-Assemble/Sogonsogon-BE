package com.sparta.sogonsogon.radio.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Timestamped {
    @CreatedDate
    private LocalDateTime created_At; // 생성시간

    @CreatedDate
    private LocalDateTime start_Time; // 방송시작시간

    @LastModifiedDate
    private LocalDateTime end_Time; // 방송종료 시간
}
