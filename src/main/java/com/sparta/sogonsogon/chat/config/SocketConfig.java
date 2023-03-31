package com.sparta.sogonsogon.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        registry.addEndpoint("/webSocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
//                .setHeartbeatTime(2000);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 해당 주소를 구독하고 있는 클라이언트들에게 메세지 전달
        registry.enableSimpleBroker("/chat","/queue");
        //클라이언트에서 보낸 메세지를 받을 prefix
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
