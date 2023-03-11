package com.sparta.sogonsogon.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class StatusResponseDto<T> {


    private int statusCode;
    private String message;
    private T data;

    public StatusResponseDto(int statusCode, String message, T data){
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> StatusResponseDto<T> success(T data){
        return new StatusResponseDto<>(HttpStatus.OK.value(), "success", data);
    }

    public static <T> StatusResponseDto<T> fail(int statusCode, T data){
        return new StatusResponseDto<>(statusCode, "fail", data);
    }
}
