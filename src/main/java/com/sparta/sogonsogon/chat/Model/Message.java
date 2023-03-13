package com.sparta.sogonsogon.chat.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Message {

    private String username;
    private String content;
    private Date date;

    public Message(String username, String content, Date date) {
        this.username = username;
        this.content = content;
        this.date = date;
    }

}