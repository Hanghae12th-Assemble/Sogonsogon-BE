package com.sparta.sogonsogon.radio.controller;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.radio.service.RadioService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/radios")
public class RadioController {
    private final RadioService radioService;
//    private final RadioRepository radioRepository;

    @PostMapping(value = "/",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "라디오  생성", description ="라디오 생성" )
    public StatusResponseDto<RadioResponseDto> createRadio(@RequestBody @Valid @ModelAttribute RadioRequestDto requestDto,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return StatusResponseDto.success(HttpStatus.CREATED, radioService.createRadio(requestDto,userDetails));
    }

    @GetMapping("/")
    @Operation(summary = "전체 라디오 조회", description ="전체 라디오 조회" )
    public StatusResponseDto<List<RadioResponseDto>> getRadios(){
        return StatusResponseDto.success(HttpStatus.OK, radioService.findAllRadios());
    }
//
//    @GetMapping("/{radioId}")
//    @Operation(summary = "선택된 라디오 조회", description ="선택된 라디오 조회" )
//    public StatusResponseDto<RadioResponseDto> getRadio(@PathVariable Long radioId){
//        return StatusResponseDto.success(HttpStatus.OK, radioService.findRadio(radioId));
//    }
//
//    @PutMapping(value = "/{radioId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "선택된 라디오 정보 수정", description ="선택된 라디오 정보 수정" )
//    public StatusResponseDto<RadioResponseDto> updateRadio(@PathVariable Long radioId,
//                                                           @Valid @ModelAttribute RadioRequestDto requestDto,
//                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
//        return StatusResponseDto.success(HttpStatus.OK, radioService.updateRadio(radioId,requestDto,userDetails));
//    }


    @DeleteMapping("/{radioId}")
    @Operation(summary = "선택된 라디오 삭제", description ="선택된 라디오 삭제" )
    public StatusResponseDto<RadioResponseDto> deleteRadio(@PathVariable Long radioId,
                                                 @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        radioService.deleteRadio(radioId,userDetails.getUser());
        return StatusResponseDto.success(HttpStatus.OK,null);
    }
}
