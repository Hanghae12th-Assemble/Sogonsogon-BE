package com.sparta.sogonsogon.enums;

public enum ErrorType {

    JWT_EXCEPTION("Invalid token error"),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION("Invalid argument error"),
    DUPLICATE_KEY_EXCEPTION("Invalid key error"),
    EXCEPTION("Exception error"),
    UNAUTHORIZED_EXCEPTION("Authentication Error"),
    ILLEGAL_ARGUMENT_EXCEPTION("Wrong argument error"),
    INTERMAL_SERVER_ERROR("Internal Server Error")
    ;

    String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
