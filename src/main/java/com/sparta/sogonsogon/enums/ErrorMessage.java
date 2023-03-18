package com.sparta.sogonsogon.enums;

public enum ErrorMessage {

    FORBIDDEN("권한이 없습니다."),
    UNAUTHORIZED("토큰이 유효하지 않습니다.")
    ;

    String message;
    ErrorMessage(String description) {
        this.message = description;
    }

    public String getMessage() {
        return this.message;
    }
}
