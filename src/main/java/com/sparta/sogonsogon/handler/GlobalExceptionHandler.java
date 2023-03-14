package com.sparta.sogonsogon.handler;

import com.sparta.sogonsogon.dto.ErrorResponseDTO;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.lang.model.type.ErrorType;
import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // 이 에러가 발생했을 때
    public StatusResponseDto<ErrorResponseDTO> signupInputErrorHandle(MethodArgumentNotValidException ex) {
        // 해당하는 핸들러가 작동한다.
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " : " + error.getDefaultMessage())
            .collect(Collectors.toList());
        // 에러를 리스트에 담아 ErrorResponseDto를 생성한다.
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errors);
        return StatusResponseDto.fail(HttpStatus.BAD_REQUEST, errorResponseDTO);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public StatusResponseDto<ErrorResponseDTO> signupDuplicateErrorHandle(DuplicateKeyException ex) {
        return StatusResponseDto.fail(HttpStatus.CONFLICT, getErrorResponseDTO(ex));
    }



    @ExceptionHandler(UsernameNotFoundException.class)
    public StatusResponseDto<ErrorResponseDTO> loginUsernameNotFoundErrorHandle(UsernameNotFoundException ex) {
//        String error = ex.getMessage();
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(error);
        return StatusResponseDto.fail(HttpStatus.NOT_FOUND, getErrorResponseDTO(ex));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public StatusResponseDto<ErrorResponseDTO> loginBadPasswordErrorHandle(BadCredentialsException ex) {
//        String error = ex.getMessage();
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(error);
        ;
        return StatusResponseDto.fail(HttpStatus.UNAUTHORIZED, getErrorResponseDTO(ex));
    }

    private static ErrorResponseDTO getErrorResponseDTO(Exception ex) {
        String errors = ex.getMessage();
        return new ErrorResponseDTO(errors);
    }

//
//    @ExceptionHandler({
//        MethodArgumentNotValidException.class
//    })
//    public StatusResponseDto<?> tempHandler(MethodArgumentNotValidException exception) {
//        exception.getBindingResult()
//    }

//    @ExceptionHandler({IllegalAccessException.class,
//        NullPointerException.class,
//        UsernameNotFoundException.class,
//        AuthenticationException.class,
//        EntityNotFoundException.class,
//        AccessDeniedException.class,
//        IllegalArgumentException.class
//    })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public StatusResponseDto<?> handle(Exception ex) {
//        return StatusResponseDto.fail(400, ex.getMessage());
//    }

}
