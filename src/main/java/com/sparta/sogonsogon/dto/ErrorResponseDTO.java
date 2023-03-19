package com.sparta.sogonsogon.dto;

import com.sparta.sogonsogon.enums.ErrorType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
