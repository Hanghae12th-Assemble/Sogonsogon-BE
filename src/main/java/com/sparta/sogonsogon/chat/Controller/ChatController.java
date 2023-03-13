package com.sparta.sogonsogon.chat.Controller;


import com.sparta.sogonsogon.chat.Model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ChatController {
//todo : refactoring

    @MessageMapping("/hello")
    @SendTo("/topic/roomId")
    public Message boradCast(Message message){
        return message;
    }
}
