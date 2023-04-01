package com.sparta.sogonsogon.member.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import com.sparta.sogonsogon.member.oauth.dto.KakaoMemberInfoDto;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoMemberService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private  String kakaoApiKey;

    private  final JwtUtil jwtUtil;
    private  final PasswordEncoder passwordEncoder;
    private  final MemberRepository memberRepository;

    @Transactional
    public StatusResponseDto<MemberResponseDto> kakologin(String code, HttpServletResponse response) throws JsonProcessingException{

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);
        log.info(accessToken);

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);
        log.info(kakaoMemberInfo.toString());

        // 3. "카카오 사용자 정보"로 필요시 회원가입
        Member kakaoMember = registerKakaoUserIfNeeded(kakaoMemberInfo);
        log.info(kakaoMember.toString());

        forceLogin(kakaoMember);

        String token = jwtUtil.createToken(kakaoMemberInfo.getEmail(), MemberRoleEnum.SOCIAL);
        log.info(token);

        response.addHeader("Authorization", token);

        return StatusResponseDto.success(HttpStatus.OK, new MemberResponseDto(kakaoMember));
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoApiKey); //Rest API 키
        body.add("redirect_uri", "https://localhost:3000/login/oauth2/code/kakao");// 수정 필요 우리 것으로 콜백 링크 만들어야 함
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        log.info(jsonNode.get("access_token").asText());
        return jsonNode.get("access_token").asText();
    }

    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String profileImage = jsonNode.get("kakao_account")
                .get("profile").get("profile_image_url").asText();
        log.info(id.toString());
        log.info(email.toString());
        log.info(nickname.toString());
        log.info(profileImage.toString());
        return new KakaoMemberInfoDto(id, nickname, email, profileImage);
    }


    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoMemberInfo.getId();
        log.info(kakaoId.toString());
        Member kakaoMember = memberRepository.findByKakaoId(kakaoId).orElse(null);
        log.info(kakaoMemberInfo.getEmail().toString());
        if (kakaoMember == null) {
            // 카카오 사용자 이메일과 동일한 이메일을 가진 회원이 있는지 확인
            String kakaoEmail = kakaoMemberInfo.getEmail();
            log.info(kakaoEmail);
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoMember = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoMember.setKakaoId(kakaoId);
            } else {
                // 신규 회원가입
                // username: kakao nickname
                String nickname = kakaoMemberInfo.getNickname();
//                int count = memberRepository.countByNickname(nickname);
//                nickname = count == 0 ? nickname : nickname + count;

                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoMemberInfo.getEmail();

                // 프로필 사진 가져오기
                String profileImage = kakaoMemberInfo.getProfileImageUrl();
                //고유 아이디 랜덤 생성
                String membername =email.substring(0, email.indexOf('@'));
                kakaoMember = new Member(nickname,profileImage, encodedPassword, email, kakaoId, membername);

            }
            log.info(kakaoMember.toString());
            memberRepository.save(kakaoMember);
        }
        return kakaoMember;
    }

    private void forceLogin(Member kakaoMember) {
        UserDetails userDetails = new UserDetailsImpl(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
