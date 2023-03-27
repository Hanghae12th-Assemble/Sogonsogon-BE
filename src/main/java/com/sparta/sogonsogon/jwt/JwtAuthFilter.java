package com.sparta.sogonsogon.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor //서비스 생성할 때 겸해서 같이 같이온다(repository)
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        //회원가입할 때는 토큰이 필요하지 않으므로 분기처리 필요
        if (jwtUtil.validateToken(token)) {
            String info = jwtUtil.getEmailFromToken(token);
            setAuthentication(info);
        }
        filterChain.doFilter(request,response);
    }

    //Authentication -> context -> SecurityContextHolder
    public void setAuthentication(String membername) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(membername);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }



}
