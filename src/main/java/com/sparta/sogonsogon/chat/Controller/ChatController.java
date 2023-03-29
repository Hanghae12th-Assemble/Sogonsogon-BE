package com.sparta.sogonsogon.chat.Controller;


import com.sparta.sogonsogon.chat.dto.ChattingDto;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private final MemberRepository memberRepository;

    @MessageMapping("/{radioId}")
    @SendTo("/chat/{radioId}")
    @ApiOperation(value = "chatting", notes = "라디오 채팅 기능")
    public void createChat(@DestinationVariable Long radioId, @Payload ChattingDto chattingDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("CHAT {}", chattingDto);

        ChattingDto chat = ChattingDto.builder()
                .type(TALK)
                .sender(userDetails.getUsername())
                .message(chattingDto.getMessage())
                .radioId(radioId)
                .build();
        template.convertAndSend("/chat/" + radioId, chat);
    }
}
