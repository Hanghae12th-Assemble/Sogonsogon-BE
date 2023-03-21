package com.sparta.sogonsogon.noti.service;

import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.noti.dto.NotificationResponseDto;
import com.sparta.sogonsogon.noti.entity.Notification;
import com.sparta.sogonsogon.noti.repository.EmitterRepository;
import com.sparta.sogonsogon.noti.repository.NotificationRepository;
import com.sparta.sogonsogon.noti.util.AlarmType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmitterRepository emitterRepository;

    private final NotificationRepository notificationRepository;


    //DEFAULT_TIMEOUT을 기본값으로 설정
    private static final Long DEFAULT_TIMEOUT = 60 * 1000L;




    // SSE 연결을 시작하는 메서드인 subscribe()를 구현한 코드********************************
    // SSE 연결을 시작하려면, SseEmitter 객체를 생성
    // SseEmitter는 생성되면 일정 시간이 지나면 연결이 끊어지도록 설정
    // 이 예제에서는 60초 동안 응답이 없으면 연결이 끊어지도록 설정하였습니다.

    public SseEmitter subscribe(Long memberId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 시간이 만료된 경우에 대해 자동으로 레포지토리에서 삭제 처리해줄 수 있는 콜백을 등록해놓을 수 있다.
        //emitter의 onCompletion()과 onTimeout() 메서드를 이용하여 연결이 종료될 때 emitterRepository에서 emitter를 삭제하도록 설정
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // SSE 연결이 시작되면, 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(memberId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");

//        // 클라이언트가 미수신한 Event 목록이 존재할 경우 sendLostData() 메서드를 이용하여 재전송함.
//        if (hasLostData(lastEventId)) {
//            sendLostData(lastEventId, memberId, emitterId, emitter);
//        }

        return emitter;
        // 마지막으로, emitter를 반환하여 SSE 연결을 완료
    }



    // SSE에서 사용되는 SseEmitter 객체를 생성하고 이를 통해 클라이언트로 알림을 전송하는 역할을 담당**********************
    private String makeTimeIncludeId(Long memberId) {
        //makeTimeIncludeId 메서드는 현재 시간을 포함한 멤버 ID를 생성해 반환하는 메서드입니다.
        // 이를 통해 누가 구독 중인지에 대한 구분을 하여 SseEmitter 객체를 식별
        return memberId + "_" + System.currentTimeMillis();
    }


    //sendNotification 메서드는 생성한 SseEmitter 객체를 통해 클라이언트로 알림을 전송**************************
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            //SseEmitter 객체의 send() 메서드를 호출하여 이벤트 데이터를 전송
            //eventId는 이벤트 ID를 나타내며, data는 전송할 데이터
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            //만약 IOException 예외가 발생하면, 클라이언트와의 연결이 끊어졌다는 뜻이므로 emitter를 삭제합니다.
            // 이렇게 함으로써, 알림을 전송할 emitter가 없어지지 않도록 하며, 불필요한 리소스 낭비를 방지
            emitterRepository.deleteById(emitterId);
        }
    }


    // *********************************************
    //SSE 스트림에서 이전 이벤트가 누락되었는지 여부를 확인하고 누락된 이벤트를 다시 전송하는 기능을 수행
    private boolean hasLostData(String lastEventId) {
        //hasLostData 메서드는 lastEventId가 비어있지 않으면 true를 반환하고, 누락된 데이터가 있음을 나타냅니다.
        // lastEventId는 클라이언트가 마지막으로 받은 이벤트의 ID를 나타냅니다.
        return !lastEventId.isEmpty();
    }

    //클라이언트가 SSE 스트림을 받지 못하거나 연결이 끊어졌을 때 누락된 데이터를 재전송하여 데이터 유실을 방지할 수 있다. *****************
    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        //sendLostData 메서드는 누락된 데이터를 찾아서 재전송함.
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
        //emitterRepository에서 해당 멤버 ID에 대한 이벤트 캐시를 모두 가져
        //그리고 lastEventId와 비교하여 누락된 데이터를 필터링하고,
        // 해당 데이터를 sendNotification 메서드를 통해 emitter로 다시 전송
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }



    //SSE를 이용하여 알림(Notification) 메시지를 구독(subscribe)한 클라이언트에게 전송하는 기능을 구현한 메서드이다.
    //이를 통해 클라이언트는 Notification을 실시간으로 받을 수 있게 됩니다.
    public void send(Member receiver, AlarmType alarmType, String message) {
        //send() 메서드는 Member 객체와 AlarmType 열거형, 알림 메시지(String)와 알림 상태(Boolean) 값을 인자로 받아 기능을 구현한다.
        Notification notification = notificationRepository.save(createNotification(receiver, alarmType,message));

        // Notification 객체의 수신자 ID를 추출하고,
        String receiverId = String.valueOf(receiver.getId());
        // 현재 시간을 포함한 고유한 eventId를 생성한다.
        String eventId = receiverId + "_" + System.currentTimeMillis();

        //수신자 ID를 key로 하여 emitterRepository에서 해당 수신자에 대한 모든 SseEmitter 객체를 가져와서
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach(
                //forEach로 각각의 emitter에 대해
                //emitterRepository를 통해 SseEmitter 객체에 해당 Notification을 캐시합니다.
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);

                    //sendNotification() 메서드를 호출하여, SseEmitter 객체에 해당 Notification을 전송한다.
                    //이 때 eventId와 emitterId도 함께 전송되며,
                    // Notification 객체를 클라이언트 측에서 처리할 수 있도록 변환된 NotificationResponseDto 객체를 함께 전송
                    sendNotification(emitter, eventId, key, NotificationResponseDto.create(notification));
                }
        );
    }

    private Notification createNotification(Member receiver, AlarmType alarmType, String message) {
        return Notification.builder()
                .receiver(receiver)
                .readState(false)
                .alarmType(alarmType)
                .message(message)
                .build();
    }

}
