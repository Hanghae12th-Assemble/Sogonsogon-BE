package com.sparta.sogonsogon.noti.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.noti.dto.NotificationResponseDto;
import com.sparta.sogonsogon.noti.repository.NotificationRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import com.sparta.sogonsogon.noti.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequiredArgsConstructor //생성자를 자동으로 생성
@RequestMapping("/api/notificaiton")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;
    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @Operation(summary = "알림 구독", description = "알림 구독")
    @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)///subscribe 엔드포인트로 들어오는 요청을 처리. produces 속성은 해당 메서드가 반환하는 데이터 형식을 지정
    @ResponseStatus(HttpStatus.OK)
    // 해당 메서드가 반환하는 HTTP 응답 코드를 지정합니다.
    // 이 경우 HttpStatus.OK 즉, 200을 반환합니다.
    public SseEmitter subscribe(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.subscribe( userDetails.getUser().getId() );
    }
    //@ApiOperation 어노테이션은 Swagger를 사용하여 API 문서를 생성하는 데 사용됩니다.
// 이 어노테이션은 "알림 구독"이라는 이름과 함께 간단한 설명을 제공합니다.
//
//@GetMapping 어노테이션은 HTTP GET 요청을 처리하는 메서드를 지정합니다.
// 이 메서드는 "/" 경로에 대한 GET 요청을 처리합니다. produces 속성은 이 메서드가 반환하는 데이터의 유형을 나타냅니다.
// 이 경우, "text/event-stream"은 SSE (Server-Sent Events) 스트림을 응답으로 반환한다는 것을 의미합니다.
//
//@ResponseStatus 어노테이션은 이 메서드가 반환하는 HTTP 응답 코드를 나타냅니다.
// 이 경우, HttpStatus.OK (즉, 200)을 반환합니다.
//
//subscribe 메서드는 SseEmitter 객체를 반환합니다. 이 객체는 SSE 스트림의 역할을 합니다.
// 구독자는 이 엔드포인트를 통해 SSE 스트림에 구독할 수 있습니다.
//
//@Parameter 어노테이션은 Swagger를 사용하여 API 문서를 생성하는 데 사용됩니다.
// 이 경우, hidden = true로 설정되어 있으므로 Swagger 문서에서 숨겨진 매개 변수로 표시됩니다.
//
//@AuthenticationPrincipal 어노테이션은 Spring Security를 사용하여 인증된 사용자의 정보를 가져올 때 사용됩니다.
// 이 경우, UserDetailsImpl 객체가 반환됩니다. UserDetailsImpl 객체에는 인증된 사용자의 ID 등의 정보가 포함됩니다.
//
//notificationService.subscribe() 메서드는 구독자를 등록하고, SSE 스트림을 반환합니다.
// 구독자는 SSE 스트림에서 알림을 수신합니다. 이 메서드는 구독자의 ID를 인자로 받습니다.

//    @GetMapping("/")
//    @Operation(summary = "전체 알림 보여주기", description = "전체 알림 보여주기")
//    public StatusResponseDto<List<NotificationResponseDto>> showNotificaion() {
//        return StatusResponseDto.success(HttpStatus.OK, nitification.findAllRadios());
//    }


    @Operation(summary = "받은 알림 전체 조회", description = "받은 알림 전체 조회")
    @GetMapping("/AllNotifications")
    public StatusResponseDto<List<NotificationResponseDto>> getAllNotifications(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        List<Notification> notifications = notificationService.getNotificationsByReceiverId(receiverId, userDetails);
//        return notifications.stream()
//                .map(NotificationResponseDto::create)
//                .collect(Collectors.toList());
        return StatusResponseDto.success(HttpStatus.OK, notificationService.getAllNotifications(userDetails.getUser().getId()));
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "받은 알림 선택하여 조회", description = "받은 알림 선택하여 조회")
    public NotificationResponseDto getNotification(@PathVariable Long notificationId,
                                                   @ApiIgnore @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getNotification(notificationId,userDetails);
    }

    @PutMapping("/{notificationId}/confirm")
    @Operation(summary = "알림확인", description = "알림확인")
    public StatusResponseDto<NotificationResponseDto> confirmNotification(@PathVariable Long notificationId,
                                                                          @ApiIgnore @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
                                                    ) {
        NotificationResponseDto notificationResponseDto = notificationService.confirmNotification(userDetails.getUser(),notificationId);
        return StatusResponseDto.success(HttpStatus.OK, notificationResponseDto);
//        return ResponseEntity.ok("Notification confirmed successfully");

    }
}
