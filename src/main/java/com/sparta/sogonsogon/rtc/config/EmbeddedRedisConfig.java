package com.sparta.sogonsogon.rtc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RequiredArgsConstructor
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

    private final RedisProperties redisProperties;
    private RedisServer redisServer;

    @PostConstruct
    public void redisServer(){
        redisServer = new RedisServer(redisProperties.getPort());
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis(){
        if (redisServer != null){
            redisServer.stop();
        }
    }

}
