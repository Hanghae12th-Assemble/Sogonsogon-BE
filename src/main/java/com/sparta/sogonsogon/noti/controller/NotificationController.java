package com.sparta.sogonsogon.noti.controller;


import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import com.sparta.sogonsogon.noti.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
//@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userDetails.getUser().getId(), lastEventId);
    }

    //    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails)  {
//        return notificationService.subscribe(userDetails.getUser().getId());
//    }
//
    //SSE 구독 요청
//    @ApiOperation(value = "SSE 구독 요청", notes = "실시간 알림을 받기 위한 구독 신청")
//    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter connect(@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//        return sseService.subscribe(userDetails.getAccount().getId());
//    }
}

//실제 클라이언트로부터 오는 알림 구독 요청을 받는다.
//현재 누구로부터 온 알림 구독인지에 대한 부분은 @AuthenticationPrincipal을 활용해 입력받는다. (현재 Spring Security를 이용 중이기 때문에 이에 대한 정보를 받아올 수 있다.)
//이전에 받지 못한 정보가 있다면, Last-Event-ID라는 헤더와 함께 날아오므로 이에 대한 정보를 받아주도록 한다. (항상 날아오는 정보가 아니기 때문에)
//그리고 실제 구독하는 작업을 진행한다.



