package com.sparta.sogonsogon.noti.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;



// SseEmitter를 이용해 알림을 실제로 보내게 되는데 어떤 회원에게 어떤 Emitter가 연결되어있는지를 저장해줘야하고 어떤 이벤트들이 현재까지 발생했는지에 대해서도 저장하고 있어야 한다.
// (추후 Emitter의 연결이 끊기게 되면 저장되어 있는 Event를 기반으로 이를 전송해줄 수 있어야 되기 때문이다.)
//그렇기 때문에 추가적으로 EmitterRepository를 추가적으로 구현해주었다.

// 현재는 Map을 이용해 Emitter와 이벤트를 저장하는 형식으로 구현하였으나 추후 다른 방식으로 이를 구현할 수 있기 때문에 유연한 전환을 위해 추상체를 생성하고
@Repository
public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter); //Emitter를 저장한다.

    void saveEventCache(String emitterId, Object event); //이벤트를 저장한다.


    //브라우저당 여러 개 연결이 가능하기에 여러 Emitter가 존재할 수 있다.
    //  해당 회원과 관련된 모든 Emitter를 찾는다.
    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);


    //해당 회원과 관련된 모든 이벤트를 찾는다.
    Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId);

    //Emitter를 지운다.
    void deleteById(String id);

    //해당 회원과 관련된 모든 Emitter를 지운다.
    void deleteAllEmitterStartWithId(String memberId);

    //해당 회원과 관련된 모든 이벤트를 지운다.
    void deleteAllEventCacheStartWithId(String memberId);
//
//    Map<String, Object> findAllEventCacheStartWithId(String valueOf);
//
//    Map<String, SseEmitter> findAllStartWithById(String id);
}
