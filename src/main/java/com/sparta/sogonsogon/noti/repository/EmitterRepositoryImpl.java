package com.sparta.sogonsogon.noti.repository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


//동시성을 고려하여 ConcurrentHashMap을 이용해 구현해주고 이를 저장하고 꺼내는 식의 방식을 진행한다.
//Emitter와 이벤트를 찾는 부분에 있어 startsWith을 사용하는 이유는 현재 저장하는데 있어
// 뒤에 구분자로 회원의 ID를 사용하기 때문에 해당 회원과 관련된 Emitter와 이벤트들을 찾아오는 것이다.
@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

//    @Override
//    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
//
//        return emitters.entrySet().stream()
//                .filter(entry -> Arrays.stream(entry.getKey().split("_")).findFirst().get().equals(memberId))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//
//    @Override
//    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
//        return eventCache.entrySet().stream()
//                .filter(entry -> Arrays.stream(entry.getKey().split("_")).findFirst().get().equals(memberId))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (Arrays.stream(key.split("_")).findFirst().get().equals(memberId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }

}
