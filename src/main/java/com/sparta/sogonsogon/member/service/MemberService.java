package com.sparta.sogonsogon.member.service;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.follow.repository.FollowRepository;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.dto.*;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import com.sparta.sogonsogon.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final JwtUtil jwtUtil;


    //회원 가입
    @Transactional
    public MemberResponseDto signup(SignUpRequestDto requestDto) throws IllegalAccessException {
        String membername = requestDto.getMembername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();

        Optional<Member> foundMembername = memberRepository.findByMembername(membername);
        if(foundMembername.isPresent()){
            throw new DuplicateKeyException(ErrorMessage.DUPLICATE_USERNAME.getMessage()); // HTTP 409 Conflict
        }
        Optional<Member> foundEmail = memberRepository.findByEmail(email);
        if(foundEmail.isPresent()){
            throw new DuplicateKeyException(ErrorMessage.DUPLICATE_EMAIL.getMessage());
        }

        Member member = new Member(requestDto, password);
        memberRepository.save(member);
        return new MemberResponseDto(member);
    }

    //로그인
    @Transactional(readOnly = true)
    public MemberResponseDto login(LoginRequestDto requestDto, HttpServletResponse response)  {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException(ErrorMessage.WRONG_USERNAME.getMessage()) // HTTP 404 Not Found
        );

        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new BadCredentialsException(ErrorMessage.WRONG_PASSWORD.getMessage()); // HTTP 401 Unauthorized
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getMembername(), member.getRole()));
        return new MemberResponseDto(member);

    }

    // 회원 정보 수정
    @Transactional
    public StatusResponseDto<MemberResponseDto> update(Long id, MemberRequestDto memberRequestDto, UserDetailsImpl userDetails) throws IOException {

        Member member= memberRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException(ErrorMessage.WRONG_USERNAME.getMessage())
        );

        if ((member.getRole() == MemberRoleEnum.USER || member.getRole() == MemberRoleEnum.SOCIAL)&& member.getMembername().equals(userDetails.getUser().getMembername())){
            member.update( memberRequestDto);
            return StatusResponseDto.success(HttpStatus.OK, new MemberResponseDto(member));
        }else{
            throw new IllegalArgumentException(ErrorMessage.ACCESS_DENIED.getMessage());
        }
    }

    // 고유 아이디로 유저 정보 조회
    public StatusResponseDto<List<MemberResponseDto>> getInfoByMembername(String membername) {
        List<Member> list = memberRepository.findAllByMembernameContaining(membername);
        List<MemberResponseDto> memberResponseDto = new ArrayList<>();
            for (Member member: list) {
                memberResponseDto.add(new MemberResponseDto(member));
            }
            return StatusResponseDto.success(HttpStatus.OK, memberResponseDto);
    }

    //유저 닉네임으로 정보 조회
    @Transactional
    public StatusResponseDto<List<MemberOneResponseDto>> getListByNickname(String nickname) {
        log.info(nickname);
        List<Member> memberlist = memberRepository.searchAllByNicknameLike(nickname);
        log.info(memberlist.toString());
        List<MemberOneResponseDto> memberResponseDtos = new ArrayList<>();
        for (Member member : memberlist) {
            memberResponseDtos.add(MemberOneResponseDto.of(member));
        }
        log.info(memberResponseDtos.toString());
        return StatusResponseDto.success(HttpStatus.OK, memberResponseDtos);
    }

    public StatusResponseDto<MemberResponseDto> detailsMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                ()-> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. ")
        );
        return StatusResponseDto.success(HttpStatus.OK, new MemberResponseDto(member));
    }
}
