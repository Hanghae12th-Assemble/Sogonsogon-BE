package com.sparta.sogonsogon.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ErrorResponseDTO {

    List<String> errorMessage;

    public ErrorResponseDTO(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorResponseDTO(String errorMessage) {
        this.errorMessage = Collections.singletonList(errorMessage);
    }
}
