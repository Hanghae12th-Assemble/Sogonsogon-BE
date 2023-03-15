package com.sparta.sogonsogon.dto;

import com.sun.net.httpserver.HttpsServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class StatusResponseDto<T> {

    enum MessageType {
        EXCEPTION("fail"),
        SUCCESS("success");

        private String message;

        MessageType(String message) {
            this.message = message;
        }
    }

    private int statusCode;
    private MessageType message;
    private T data;

    public StatusResponseDto(HttpStatus httpStatus, MessageType message, T data){
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public static <T> StatusResponseDto<T> success(HttpStatus httpStatus, T data){
        return new StatusResponseDto<>(httpStatus, MessageType.SUCCESS, data);
    }

    public static StatusResponseDto<ErrorResponseDTO> fail(HttpStatus httpStatus, ErrorResponseDTO errorResponseDTO){
        return new StatusResponseDto<>(httpStatus, MessageType.EXCEPTION, errorResponseDTO);
    }
}
