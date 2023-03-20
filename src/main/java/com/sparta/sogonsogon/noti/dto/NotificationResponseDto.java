package com.sparta.sogonsogon.noti.dto;

import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.noti.util.AlarmType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationResponseDto {


    private Long notificationId;

    private String message;

    private Boolean readStatus;

    private AlarmType alarmType;

    private String createdAt;


    @Builder
    public NotificationResponseDto(Long id, String message,Boolean readStatus,
                                   AlarmType alarmType, String createdAt) {
        this.notificationId = id;
        this.message = message;
        this.readStatus = readStatus;
        this.alarmType = alarmType;
        this.createdAt = createdAt;
    }

    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getId();
        this.message = notification.getMessage();
        this.readStatus = notification.getIsRead();
        this.alarmType = notification.getAlarmType();
//        this.createdAt = Chrono.timesAgo(notification.getCreatedAt());
    }

    public static NotificationResponseDto create(Notification notification) {
//        createdAt = Chrono.timesAgo(notification.getCreatedAt());

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .alarmType(notification.getAlarmType())
                .readStatus(notification.getIsRead())
//                .createdAt(createdAt)
                .build();
    }
}
