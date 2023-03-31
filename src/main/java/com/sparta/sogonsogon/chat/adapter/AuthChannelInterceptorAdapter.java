package com.sparta.sogonsogon.chat.adapter;
import com.sparta.sogonsogon.chat.service.WebSocketAuthenticatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ChannelInterceptor.class);

    private final WebSocketAuthenticatorService webSocketAuthenticatorService;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.setAuthenticationOrFail(accessor);
            accessor.setUser(user);
        }
        return message;
    }
}
