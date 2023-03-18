package com.sparta.sogonsogon.noti.controller;


import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import com.sparta.sogonsogon.noti.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
}

//실제 클라이언트로부터 오는 알림 구독 요청을 받는다.
//현재 누구로부터 온 알림 구독인지에 대한 부분은 @AuthenticationPrincipal을 활용해 입력받는다. (현재 Spring Security를 이용 중이기 때문에 이에 대한 정보를 받아올 수 있다.)
//이전에 받지 못한 정보가 있다면, Last-Event-ID라는 헤더와 함께 날아오므로 이에 대한 정보를 받아주도록 한다. (항상 날아오는 정보가 아니기 때문에)
//그리고 실제 구독하는 작업을 진행한다.



