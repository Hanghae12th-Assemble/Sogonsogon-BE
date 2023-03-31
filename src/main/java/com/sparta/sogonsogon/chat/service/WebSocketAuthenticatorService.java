package com.sparta.sogonsogon.chat.service;
//import chitchat.entity.user.User;
//import chitchat.oauth.token.AuthToken;
//import chitchat.oauth.token.AuthTokenProvider;
//import chitchat.utils.HeaderUtil;
import com.sparta.sogonsogon.enums.ErrorMessage;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.member.entity.Member;
import com.sparta.sogonsogon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthenticatorService {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthenticatorService.class);
    // This method MUST return a UsernamePasswordAuthenticationToken instance, the spring security chain is testing it with 'instanceof' later on. So don't use a subclass of it or any other class
    public UsernamePasswordAuthenticationToken  setAuthenticationOrFail(final StompHeaderAccessor accessor) throws AuthenticationException {
        String tokenStr = accessor.getFirstNativeHeader("Authorization");
        String emailFromToken = jwtUtil.getEmailFromToken(tokenStr);
        log.info(emailFromToken.toString());
        boolean tokenCheck = jwtUtil.validateToken(tokenStr);
        if (tokenCheck) {
            logger.info("\n\n\n{}\n\n\n", tokenStr);
            Member member = memberRepository.findByEmail(emailFromToken).orElseThrow(
                    ()-> new IllegalArgumentException(ErrorMessage.NOT_FOUND_MEMBER.getMessage())
            );
            Authentication authentication = jwtUtil.createAuthentication(member.getMembername());
            return new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    null,
                    Collections.singleton((GrantedAuthority) () -> "USER") // MUST provide at least one role
            );
        }
        else {
            throw new AuthenticationCredentialsNotFoundException("Password was null or empty.");
        }
    }
}
