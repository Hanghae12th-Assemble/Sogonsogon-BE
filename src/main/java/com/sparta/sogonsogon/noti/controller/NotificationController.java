package com.sparta.sogonsogon.noti.controller;


import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor //생성자를 자동으로 생성
@RequestMapping("/api/subscribe")
public class NotificationController {

    private final NotificationService notificationService;

    // 서버에서는 EventSource를 통해 날아오는 요청을 처리할 컨트롤러가 필요하다. *******************
    // sse 통신을 하기 위해서는 MIME 타입을 text/event-stream로 해줘야한다.

    //@ApiOperation : Swagger API 문서를 자동으로 생성하기 위해 사용
    //해당 어노테이션에는 API의 이름과 설명을 나타내는 값들이 포함
    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @GetMapping(value = "/", produces = "text/event-stream")///subscribe 엔드포인트로 들어오는 요청을 처리. produces 속성은 해당 메서드가 반환하는 데이터 형식을 지정
    @ResponseStatus(HttpStatus.OK) //해당 메서드가 반환하는 HTTP 응답 코드를 지정합니다. 이 경우 HttpStatus.OK 즉, 200을 반환합니다.
    public SseEmitter subscribe(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.subscribe( userDetails.getUser().getId() );
    }

    //@AuthenticationPrincipal 현재 로그인한 사용자의 정보를 주입. 이 메서드에서는 UserDetailsImpl 클래스를 사용하여 사용자 정보를 받아옴.
    //@RequestHeader 어노테이션은 HTTP 요청 헤더 값을 주입
    //Last-Event-ID 헤더는 이전 SSE 이벤트 ID를 나타남.해당 값을 이용하여 클라이언트가 놓친 이벤트가 있는지 체크하고, 필요한 경우 이전 이벤트부터 다시 전송함.
    //마지막으로, subscribe() 메서드는 notificationService 객체의 subscribe() 메서드를 호출하여 SSEEmitter 객체를 반환합니다.
    // 반환된 객체는 클라이언트와 서버 간의 연결을 나타내며, 이를 통해 서버는 클라이언트에게 이벤트를 보낼 수 있습니다.

}

//실제 클라이언트로부터 오는 알림 구독 요청을 받는다.
//현재 누구로부터 온 알림 구독인지에 대한 부분은 @AuthenticationPrincipal을 활용해 입력받는다. (현재 Spring Security를 이용 중이기 때문에 이에 대한 정보를 받아올 수 있다.)
//이전에 받지 못한 정보가 있다면, Last-Event-ID라는 헤더와 함께 날아오므로 이에 대한 정보를 받아주도록 한다. (항상 날아오는 정보가 아니기 때문에)
//그리고 실제 구독하는 작업을 진행한다.



