package com.sparta.sogonsogon.chat.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingDto {

    public enum MessageType{
        ENTER, TALK, LEAVE;
    }

    private String sender;
    private String message;
    private Long radioId;
    private MessageType type;
    private String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));


}
