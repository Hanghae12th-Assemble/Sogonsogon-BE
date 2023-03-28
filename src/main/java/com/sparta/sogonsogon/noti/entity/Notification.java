package com.sparta.sogonsogon.noti.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.noti.util.AlarmType;
import com.sparta.sogonsogon.radio.entity.Radio;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity(name = "notifications")
@Setter
@Getter //각 래퍼 클래스에 대한 추출 메소드이다.
@NoArgsConstructor
public class Notification extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //누구 : ~에 대한 알림이 도착했습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) //한 번 알림을 읽으면 읽음 표시되어 더이상 우리에게 반짝이지 않는다
    private Member receiver;     //    private Member receiver;
    private String senderMembername;
    private String senderNickname;
    private String senderProfileImageUrl;

    @Column
//    private Boolean isRead; //  읽었는지에 대한 여부를 나타낸다
    private Boolean isRead = false; //  읽었는지에 대한 여부를 나타낸다

    @Column(nullable = false)
    private String message;  //알림의 내용. 비어있지 않아야하며 50자 이내여야한다.

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false)
    private AlarmType alarmType; // 알림 종류에 관한 것이다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "radio_id")
    private Radio radio;



    public void setRadio(Radio radio) {
        this.radio = radio;
    }


    @Builder
    public Notification(Member receiver, Boolean readState,
                        String message, AlarmType alarmType,
                        String senderMembername,String senderNickname,
                        String senderProfileImageUrl) {
        this.receiver = receiver;
        this.isRead = readState;
        this.message = message;
        this.alarmType = alarmType;
        this.senderMembername = senderMembername;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

    public void setSenderMembername(String senderMembername) {
        this.senderMembername = senderMembername;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

//    public boolean getIsRead() {
//        return isRead=false;
//    }
//
//    public void setIsRead(boolean isRead) {
//        this.isRead = isRead;
//    }



}
