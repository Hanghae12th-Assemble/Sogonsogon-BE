package com.sparta.sogonsogon.enums;

public enum ErrorType {

    JWT_EXCEPTION("invalid token error");

    String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
