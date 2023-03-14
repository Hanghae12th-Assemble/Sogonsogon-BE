package com.sparta.sogonsogon.handler;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class,
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StatusResponseDto<?> handlerException(Exception ex){
        log.error("EX",ex);
        return StatusResponseDto.fail(500, ex.getStackTrace());
    }
//
//    @ExceptionHandler({
//        MethodArgumentNotValidException.class
//    })
//    public StatusResponseDto<?> tempHandler(MethodArgumentNotValidException exception) {
//        exception.getBindingResult()
//    }

    @ExceptionHandler({IllegalAccessException.class,
            NullPointerException.class,
            UsernameNotFoundException.class,
            AuthenticationException.class,
            EntityNotFoundException.class,
            AccessDeniedException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatusResponseDto<?> handle(Exception ex){
        return StatusResponseDto.fail(400, ex.getMessage());
    }

}
