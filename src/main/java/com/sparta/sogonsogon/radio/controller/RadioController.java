package com.sparta.sogonsogon.radio.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.enums.CategoryType;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.noti.dto.NotificationRequestDto;
import com.sparta.sogonsogon.noti.service.NotificationService;
import com.sparta.sogonsogon.radio.dto.RadioRequestDto;
import com.sparta.sogonsogon.radio.dto.RadioResponseDto;
import com.sparta.sogonsogon.radio.entity.EnterMemberResponseDto;
import com.sparta.sogonsogon.radio.entity.Radio;
import com.sparta.sogonsogon.radio.repository.RadioRepository;
import com.sparta.sogonsogon.radio.service.RadioService;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/radios")
public class RadioController {
    private final RadioService radioService;
    private final RadioRepository radioRepository;
    private final NotificationService notificationService;



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "라디오  생성", description = "라디오 생성")
    public StatusResponseDto<RadioResponseDto> createRadio(@Valid @ModelAttribute RadioRequestDto requestDto,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return StatusResponseDto.success(HttpStatus.CREATED, radioService.createRadio(requestDto, userDetails));
    }

    @GetMapping("/")
    @Operation(summary = "전체 라디오 조회", description = "전체 라디오 조회")
    public StatusResponseDto<Map<String, Object>> getRadios(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy) {
        return StatusResponseDto.success(HttpStatus.OK, radioService.findAllRadios(page -1, size, sortBy));
    }

    @GetMapping("/find/{radioId}")
    @Operation(summary = "선택된 라디오 조회", description ="선택된 라디오 조회" )
    public StatusResponseDto<RadioResponseDto> getRadio(@PathVariable Long radioId){
        return StatusResponseDto.success(HttpStatus.OK, radioService.findRadio(radioId));
    }



    @DeleteMapping("/{radioId}")
    @Operation(summary = "선택된 라디오 삭제", description = "선택된 라디오 삭제")
    public StatusResponseDto<RadioResponseDto> deleteRadio(@PathVariable Long radioId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        radioService.deleteRadio(radioId, userDetails.getUser());
        return StatusResponseDto.success(HttpStatus.OK, null);
    }


    @PostMapping("/enter/{radioId}")
    @Operation(summary = "선택한 라디오 참여", description = "선택한 라디오 참여")
    public StatusResponseDto<EnterMemberResponseDto> enterRadio(@PathVariable Long radioId,
                                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(HttpStatus.OK, radioService.enterRadio(radioId, userDetails));
    }

    @DeleteMapping("/quit/{radioId}")
    @Operation(summary = "입장한 라디오에서 퇴장", description = "입장한 라디오에서 퇴장")
    public StatusResponseDto<EnterMemberResponseDto> quitRadio(@PathVariable Long radioId,
                                                               @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        radioService.quitRadio(radioId, userDetails);
        return StatusResponseDto.success(HttpStatus.OK, null);
    }

    @GetMapping("/find")
    @Operation(summary = "타이틀 조회", description = "해당 방송중 제목에 단어가 들어간 모든 방송 조회")
    public StatusResponseDto<List<RadioResponseDto>> findBytitle(@RequestParam(value = "title") String title) {
        return StatusResponseDto.success(HttpStatus.OK, radioService.findByTitle(title));
    }

    @GetMapping("/{categoryType}")
    @Operation(summary = "라디오 카테고리 검색", description = "특정 카테고리에 속하는 방속만 조회/ sortBy = createdAt, enterCnt(청취자)")
    public StatusResponseDto<Map<String, Object>> findByCategory(@PathVariable CategoryType categoryType,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(required = false, defaultValue = "createdAt") String sortBy) {
        return StatusResponseDto.success(HttpStatus.OK, radioService.findByCategory(page-1, size, sortBy, categoryType));
    }





    // 방송 시작 및 종료 기능 추가*************************************************


    @PostMapping("/{radioId}/start")
    @Operation(summary = "라디오 방송 시작", description = "라디오 방송 시작")
    public ResponseEntity<Radio> startRadio(@PathVariable Long radioId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        radioService.startRadio(radioId,userDetails);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{radioId}/end")
    @Operation(summary = "라디오 방송 종료", description = "라디오 방송 종료")
    public ResponseEntity<Radio> endRadio(@PathVariable Long radioId,
                                          @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        radioService.endRadio(radioId,userDetails);
        return ResponseEntity.ok().build();
    }


}
