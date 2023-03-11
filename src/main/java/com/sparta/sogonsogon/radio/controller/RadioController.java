package com.sparta.sogonsogon.radio.controller;

import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.radio.dto.StatusResponseDto;
import com.sparta.sogonsogon.radio.repository.RadioRepository;
import com.sparta.sogonsogon.radio.service.RadioService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/radio")
public class RadioController {
    private final RadioService radioService;
    private final RadioRepository radioRepository;

    @PostMapping("/")
    public StatusResponseDto<RadioResponseDto> createRadio(@ModelAttribute  RadioRequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        return radioService.createRadio(requestDto,userDetails.getUsername());
    }


}
