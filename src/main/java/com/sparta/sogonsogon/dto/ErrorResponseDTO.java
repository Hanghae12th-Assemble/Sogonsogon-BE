package com.sparta.sogonsogon.dto;

import lombok.Getter;
import lombok.Setter;

import javax.lang.model.type.ErrorType;

@Getter
@Setter
public class ErrorResponseDTO {

    ErrorType errorType;
    String errorMessage;

    public ErrorResponseDTO(ErrorType errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }
}
