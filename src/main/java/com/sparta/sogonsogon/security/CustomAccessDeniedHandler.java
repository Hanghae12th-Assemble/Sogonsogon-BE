package com.sparta.sogonsogon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sogonsogon.dto.ErrorResponseDTO;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final StatusResponseDto dto = StatusResponseDto.fail(HttpStatus.FORBIDDEN,new ErrorResponseDTO("권한이 없습니다."));

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, dto);
            os.flush();
        }
    }
}
