package com.sparta.sogonsogon.handler;

import com.sparta.sogonsogon.dto.ErrorResponseDTO;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler // 이 에러가 발생했을 때
    private StatusResponseDto<ErrorResponseDTO> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        // 해당하는 핸들러가 작동한다.
        return StatusResponseDto.fail(HttpStatus.BAD_REQUEST, getErrorResponseDTO(ErrorType.METHOD_ARGUMENT_NOT_VALID_EXCEPTION, ex));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    private StatusResponseDto<ErrorResponseDTO> duplicateKeyExceptionHandler(DuplicateKeyException ex) {
        return StatusResponseDto.fail(HttpStatus.CONFLICT, getErrorResponseDTO(ErrorType.DUPLICATE_KEY_EXCEPTION, ex));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class,
        UsernameNotFoundException.class})
    private StatusResponseDto<ErrorResponseDTO> notfoundExceptionHandler(Exception ex) {
        return StatusResponseDto.fail(HttpStatus.NOT_FOUND, getErrorResponseDTO(ErrorType.EXCEPTION, ex));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class,
        BadCredentialsException.class,
        AuthenticationException.class})
    private StatusResponseDto<ErrorResponseDTO> unauthorizedExceptionHandler(Exception ex) {
        return StatusResponseDto.fail(HttpStatus.UNAUTHORIZED, getErrorResponseDTO(ErrorType.UNAUTHORIZED_EXCEPTION,ex));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    private StatusResponseDto<ErrorResponseDTO> illegalArgumentExceptionHander(IllegalArgumentException ex) {
        return StatusResponseDto.fail(HttpStatus.BAD_REQUEST, getErrorResponseDTO(ErrorType.ILLEGAL_ARGUMENT_EXCEPTION,ex));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({IllegalAccessException.class,
        AccessDeniedException.class})
    private StatusResponseDto<ErrorResponseDTO> forbiddenExceptionHandler(Exception ex) {
        return StatusResponseDto.fail(HttpStatus.FORBIDDEN, getErrorResponseDTO(ErrorType.EXCEPTION,ex));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    private StatusResponseDto<ErrorResponseDTO> internalServerErrorHandler(HttpServerErrorException.InternalServerError ex) {
        return StatusResponseDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, getErrorResponseDTO(ErrorType.INTERMAL_SERVER_ERROR,ex));
    }

    private static ErrorResponseDTO getErrorResponseDTO(ErrorType errorType, Exception ex) {

        if (ex instanceof MethodArgumentNotValidException validationException) {
            StringBuilder errors = new StringBuilder();

            for (FieldError fieldError : validationException.getBindingResult().getFieldErrors()) {
                errors.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(" ");
            }

            return new ErrorResponseDTO(errorType, errors.toString());
        }

        String errors = ex.getMessage();

        return new ErrorResponseDTO(errorType, errors);
    }

}
