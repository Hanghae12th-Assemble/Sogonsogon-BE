package com.sparta.sogonsogon.chat.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Chat {

    private String username;
    private String content;
    private Date date;

    public Chat(String username, String content, Date date) {
        this.username = username;
        this.content = content;
        this.date = date;
    }

}