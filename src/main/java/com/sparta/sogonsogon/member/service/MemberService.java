package com.sparta.sogonsogon.member.service;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.dto.LoginRequestDto;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.dto.SignUpRequestDto;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
//    private final FollowRepository followRepository;
    private final JwtUtil jwtUtil;
//    private final S3UploadService s3UploadService;

    @Transactional
    public StatusResponseDto<String> signup(@Valid SignUpRequestDto requestDto) throws IllegalAccessException {
        String membername = requestDto.getMembername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();

        Optional<Member> foundMembername = memberRepository.findByMembername(membername);
        if(foundMembername.isPresent()){
            throw new IllegalAccessException("중복된 아이디가 존재합니다.");
        }
        Optional<Member> foundEmail = memberRepository.findByEmail(email);
        if(foundEmail.isPresent()){
            throw new IllegalAccessException("중복된 이메일이 존재합니다.");
        }

        Member member = new Member(membername, password, nickname, email);
        memberRepository.save(member);
        return StatusResponseDto.success("회원가입 성공!");
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<MemberResponseDto> login(LoginRequestDto requestDto, HttpServletResponse response) throws IllegalAccessException {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다.")
        );

        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new IllegalAccessException("비밀번호가 틀렸습니다. 다시 입력해주세요");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getMembername(), member.getRole()));
        return StatusResponseDto.success(new MemberResponseDto(member));

    }
}
