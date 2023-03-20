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
    private static final Long DEFAULT_TIMEOUT = 60 * 1000L;



//    private Long timeout;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 시간이 만료된 경우에 대해 자동으로 레포지토리에서 삭제 처리해줄 수 있는 콜백을 등록해놓을 수 있다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(memberId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, memberId, emitterId, emitter);
        }

        return emitter;
    }

    // 누가 사용하고 있는지에 대한 SseEmitter인지를 구분해줄 필요성이 존재한다.
    private String makeTimeIncludeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(Member receiver, AlarmType alarmType, String message,Boolean isState) {
        Notification notification = notificationRepository.save(createNotification(receiver, alarmType,message,isState));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.create(notification));
                }
        );
    }

    private Notification createNotification(Member receiver, AlarmType alarmType, String message, Boolean isState) {
        return Notification.builder()
                .receiver(receiver)
                .isState(false)
                .alarmType(alarmType)
                .message(message)
                .build();
    }

//    @Override
//    public void send(Member receiver, Radio radio, String content) {
//        Notification notification = createNotification(receiver, radio, content);
//        String id = String.valueOf(receiver.getId());
//
//        // 로그인 한 유저의 SseEmitter 모두 가져오기
//        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
//        sseEmitters.forEach(
//                (key, emitter) -> {
//                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
//                    emitterRepository.saveEventCache(key, notification);
//                    // 데이터 전송
//                    sendToClient(emitter, key, NotificationResponse.from(notification));
//                }
//        );
//    }
//
//    private Notification createNotification(Member receiver, Radio review, String content) {
//        return Notification.builder()
//                .receiver(receiver)
//                .content(content)
//                .review(review)
//                .url("/reviews/" + review.getId())
//                .isRead(false)
//                .build();
//    }
//
//    private void sendToClient(SseEmitter emitter, String id, Object data) {
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(id)
//                    .name("sse")
//                    .data(data));
//        } catch (IOException exception) {
//            emitterRepository.deleteById(id);
//            throw new RuntimeException("연결 오류!");
//        }
//    }

}
