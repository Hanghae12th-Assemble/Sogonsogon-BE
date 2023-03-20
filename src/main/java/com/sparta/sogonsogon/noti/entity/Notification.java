package com.sparta.sogonsogon.noti.entity;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.noti.util.AlarmType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter //각 래퍼 클래스에 대한 추출 메소드이다.
@Setter
@Entity
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //누구 : ~에 대한 알림이 도착했습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) //한 번 알림을 읽으면 읽음 표시되어 더이상 우리에게 반짝이지 않는다
    private Member receiver;     //    private Member receiver;

    @Column(nullable = false)
    private Boolean isRead; //  읽었는지에 대한 여부를 나타낸다

    @Column(nullable = false)
    private String message;  //알림의 내용. 비어있지 않아야하며 50자 이내여야한다.

//    @Embedded
//    private NotificationContent content; //어떤 내용의 알림인지 (내용)

//    @Embedded
//    private RelatedURL url;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // 알림 종류에 관한 것이다.

    @Builder
    public Notification(Member receiver, Boolean isState,
                        String message, AlarmType alarmType) {
        this.receiver = receiver;
        this.isRead = isState;
        this.message = message;
        this.alarmType = alarmType;
    }

//    public void changeState() {
//        isRead = true;
//    }
}
