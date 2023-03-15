package com.sparta.sogonsogon.member.service;

import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.follow.repository.FollowRepository;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.dto.LoginRequestDto;
import com.sparta.sogonsogon.member.dto.MemberRequestDto;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.dto.SignUpRequestDto;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import com.sparta.sogonsogon.util.S3Uploader;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
//    private final FollowRepository followRepository;
    private final JwtUtil jwtUtil;
    private final S3Uploader s3Uploader;

    @Transactional
    public MemberResponseDto signup(SignUpRequestDto requestDto) throws IllegalAccessException {
        String membername = requestDto.getMembername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();

        Optional<Member> foundMembername = memberRepository.findByMembername(membername);
        if(foundMembername.isPresent()){
            throw new DuplicateKeyException("중복된 아이디가 존재합니다."); // HTTP 409 Conflict
        }
        Optional<Member> foundEmail = memberRepository.findByEmail(email);
        if(foundEmail.isPresent()){
            throw new DuplicateKeyException("중복된 이메일이 존재합니다.");
        }

        Member member = new Member(requestDto, password);
        memberRepository.save(member);
        return new MemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) throws IllegalAccessException {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.") // HTTP 404 Not Found
        );

        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new BadCredentialsException("비밀번호가 틀렸습니다. 다시 입력해주세요"); // HTTP 401 Unauthorized
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getMembername(), member.getRole()));
        return new MemberResponseDto(member);

    }

    // 회원 정보 수정
    public StatusResponseDto<MemberResponseDto> update(Long id, MemberRequestDto memberRequestDto, UserDetailsImpl userDetails) throws IOException {
        String profileImageUrl = s3Uploader.uploadFiles(memberRequestDto.getProfileImageUrl(), "profileImages");

        Member member= memberRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다. 다시 로그인 해주세요")
        );

        if (member.getRole() == MemberRoleEnum.USER || member.getMembername().equals(userDetails.getUser().getMembername())){
            member.update(memberRequestDto.getNickname(), memberRequestDto.getMemberInfo(), profileImageUrl, memberRequestDto.getPassword());
            return StatusResponseDto.success(HttpStatus.OK, new MemberResponseDto(member));
        }else{
            throw new IllegalArgumentException("해당 유저만 회원 정보 수정이 가능합니다. ");
        }
    }
}
