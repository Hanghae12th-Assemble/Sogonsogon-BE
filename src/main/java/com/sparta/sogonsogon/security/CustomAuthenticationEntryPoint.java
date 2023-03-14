package com.sparta.sogonsogon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sogonsogon.dto.ErrorResponseDTO;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final StatusResponseDto dto = StatusResponseDto.fail( HttpStatus.UNAUTHORIZED,new ErrorResponseDTO(new ArrayList<>(Collections.singleton("토큰이 유효하지 않습니다."))));

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, dto);
            os.flush();
        }
    }
}
