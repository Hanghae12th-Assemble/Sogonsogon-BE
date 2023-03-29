package com.sparta.sogonsogon.noti.dto;

import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.noti.util.AlarmType;
import com.sparta.sogonsogon.noti.util.Chrono;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NotificationResponseDto {



    private Long notificationId;

    private String message;

    private Boolean readStatus;

    private AlarmType alarmType;

    private String createdAt;

    private String senderMembername;
    private String senderNickname;
    private String senderProfileImageUrl;


    @Builder
    public NotificationResponseDto(Long id, String message,Boolean readStatus,
                                   AlarmType alarmType, String createdAt,
                                   String senderMembername,String senderNickname,String senderProfileImageUrl) {
        this.notificationId = id;
        this.message = message;
        this.readStatus = readStatus;
        this.alarmType = alarmType;
        this.senderMembername = senderMembername;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.createdAt = createdAt;
    }

    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getId();
        this.message = notification.getMessage();
        this.readStatus = notification.getIsRead();
        this.alarmType = notification.getAlarmType();
        this.senderMembername = notification.getSenderMembername();
        this.senderNickname = notification.getSenderNickname();
        this.senderProfileImageUrl = notification.getSenderProfileImageUrl();
        this.createdAt = Chrono.timesAgo(notification.getCreatedAt());
    }

    public static NotificationResponseDto create(Notification notification) {
        String createdAt = Chrono.timesAgo(notification.getCreatedAt());

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .alarmType(notification.getAlarmType())
                .readStatus(notification.getIsRead())
                .senderMembername(notification.getSenderMembername())
                .senderNickname(notification.getSenderNickname())
                .senderProfileImageUrl(notification.getSenderProfileImageUrl())
                .createdAt(createdAt)
                .build();
    }

}
