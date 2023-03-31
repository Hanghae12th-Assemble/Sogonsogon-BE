package com.sparta.sogonsogon.member.oauth.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sparta.sogonsogon.dto.StatusResponseDto;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.dto.MemberResponseDto;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import com.sparta.sogonsogon.member.oauth.dto.NaverMemberInfoDto;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import com.sparta.sogonsogon.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverMemberService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public StatusResponseDto<MemberResponseDto> naverlogin(String code, String state, HttpServletResponse response)throws IOException{

        NaverMemberInfoDto naverMemberInfo = getNaverMemberInfo(code, state);

        String naverId = naverMemberInfo.getNaverId();
        log.info(naverId);
        Member naverMember = memberRepository.findByNaverId(naverId).orElse(null);

        if(naverMember == null){
            String naverEmail = naverMemberInfo.getEmail();
            log.info(naverEmail);
            Member sameEmailMember = memberRepository.findByEmail(naverEmail).orElse(null);

            if(sameEmailMember != null){
                naverMember = sameEmailMember;
                naverMember.setNaverId(naverId);
            }else {
                String nickname = naverMemberInfo.getNickname();

                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String profileImageUrl = naverMemberInfo.getProfileImageUrl();
                String email = naverMemberInfo.getEmail();
                String membername = email.substring(0, email.indexOf('@'));

                naverMember = Member.builder()
                        .naverId(naverId)
                        .membername(membername)
                        .email(email)
                        .profileImageUrl(profileImageUrl)
                        .nickname(nickname)
                        .password(encodedPassword)
                        .role(MemberRoleEnum.SOCIAL)
                        .build();
            }
        memberRepository.save(naverMember);
        }

        forceLogin(naverMember);

        String token = jwtUtil.createToken(naverMember.getEmail(), MemberRoleEnum.SOCIAL);

        response.addHeader("Authorization", token);

        return StatusResponseDto.success(HttpStatus.OK, new MemberResponseDto(naverMember));
    }

    // 네이버에 요청해서 데이터 전달 받는 메소드
    public JsonElement jsonElement(String reqURL, String token, String code, String state) throws IOException {

        // 요청하는 URL 설정
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // POST 요청에 필요한 데이터 저장 후 전송
        if (token == null) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&redirect_uri= https://sogonsogon-fe.vercel.app/login/oauth2/code/naver" +// 수정필수
                    "&code=" + code +
                    "&state=" + state;
            bw.write(sb);
            bw.flush();
            bw.close();
        } else {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
            log.info(result.toString());
        }
        br.close();

        // Gson 라이브러리에 포함된 클래스로 JSON 파싱
        return JsonParser.parseString(result.toString());
    }


    // 네이버에 요청해서 회원정보 받는 메소드
    public NaverMemberInfoDto getNaverMemberInfo(String code, String state) throws IOException {

        String codeReqURL = "https://nid.naver.com/oauth2.0/token";
        String tokenReqURL = "https://openapi.naver.com/v1/nid/me";


        // 코드를 네이버에 전달하여 엑세스 토큰 가져옴
        JsonElement tokenElement = jsonElement(codeReqURL, null, code, state);

        String accessToken = tokenElement.getAsJsonObject().get("access_token").getAsString();
        log.info(accessToken);

        // 엑세스 토큰을 네이버에 전달하여 유저정보 가져옴
        JsonElement userInfoElement = jsonElement(tokenReqURL, accessToken, null, null);


        String naverId = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("id"));
        log.info(naverId.toString());
        String userEmail = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("email"));
        log.info(userEmail.toString());
        String nickName = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("nickname"));
        log.info(nickName.toString());
        String profileImage = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("profile_image"));
        log.info(profileImage.toString());

        naverId = naverId.substring(1, naverId.length() - 1);
        userEmail = userEmail.substring(1, userEmail.length() - 1);
        nickName = nickName.substring(1, nickName.length() - 1);
        profileImage = profileImage.substring(1, profileImage.length() - 1);

        return new NaverMemberInfoDto(naverId, nickName,profileImage, userEmail, accessToken);
    }

    // 강제 로그인 처리
    private void forceLogin(Member naverAccount) {
        UserDetails userDetails = new UserDetailsImpl(naverAccount);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
