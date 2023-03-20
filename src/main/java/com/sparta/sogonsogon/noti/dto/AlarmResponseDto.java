package com.sparta.sogonsogon.noti.dto;

import com.sparta.sogonsogon.noti.util.Chrono;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmResponseDto {

    private Long radioId;

    private String title; //라디오 제목

    private String memberName;

    private String createdAt;



    public AlarmResponseDto(Radio radio) {
        this.radioId = radio.getMember().getId();
        this.title = radio.getTitle().length() > 10 ? radio.getTitle().substring(0 ,9) + "..." : radio.getTitle();
        this.memberName = radio.getMember().getMembername();
        this.createdAt = Chrono.timesAgo(radio.getCreatedAt());

    }

}
