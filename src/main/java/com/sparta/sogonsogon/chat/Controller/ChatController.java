package com.sparta.sogonsogon.chat.Controller;


import com.sparta.sogonsogon.chat.dto.ChattingDto;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.sparta.sogonsogon.chat.dto.ChattingDto.MessageType.TALK;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ChatController {
    //todo : refactoring
//    private final
//    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/{radioId}")
    @SendTo("/sub/{radioId}")
    @ApiOperation(value = "chatting", notes = "라디오 채팅 기능")
    public void createChat(@DestinationVariable Long radioId, @RequestBody String message, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChattingDto chat = ChattingDto.builder()
                .type(TALK)
                .sender(userDetails.getUser().getNickname())
                .message(message)
                .radioId(radioId)
                .build();
        log.info("CHAT {}", chat);
        template.convertAndSend("/chat/" + radioId, chat);
    }

    @PostMapping("/{radioId}/send-message")
    @ApiOperation(value = "chatting", notes = "라디오 채팅 기능 (웹소켓 정보)")
    public ResponseEntity<?> sendMessage(@PathVariable Long radioId, @RequestBody String message, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
// 이 메서드는 실제로 메시지를 보내지 않으며, Swagger에서 웹소켓 API 정보를 표시하기 위한 목적으로만 사용됩니다.

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("이 API는 웹소켓 API 정보를 위한 것이며, 실제 메시지 전송은 웹소켓을 사용하세요.");
    }
}
