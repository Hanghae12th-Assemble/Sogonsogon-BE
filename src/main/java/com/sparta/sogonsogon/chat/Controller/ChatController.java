package com.sparta.sogonsogon.chat.Controller;


import com.sparta.sogonsogon.chat.dto.ChattingDto;
import com.sparta.sogonsogon.chat.service.ChatService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ChatController {
//todo : refactoring
//    private final
    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/{radioId}")
    @SendTo("/chat/{radioId}")
    @ApiOperation(value = "chatting" ,  notes = "라디오 채팅 기능")
    public void createChat(@DestinationVariable Long radioId, ChattingDto chattingDto){
        chatService.createChat(radioId, chattingDto);
        template.convertAndSend("/chat/" + radioId, chattingDto);
    }
}
