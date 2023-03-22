package com.sparta.sogonsogon.member.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.entity.MemberRoleEnum;
import com.sparta.sogonsogon.member.oauth.dto.MemberSessionDto;
import com.sparta.sogonsogon.member.oauth.dto.UserRequestMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        MemberSessionDto memberSessionDto = userRequestMapper.toDto(oAuth2User);

        String token = jwtUtil.createToken(memberSessionDto.getMembername(), MemberRoleEnum.SOCIAL);
        log.info("{}", token);

        writeTokenResponse(response, token);

    }

    private void writeTokenResponse(HttpServletResponse response, String token) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Authentication", token);
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();

    }

}
