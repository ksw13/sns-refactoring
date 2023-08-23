package com.example.configuration;

import com.example.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@EnableRedisRepositories
@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}") private String redisHost;
    @Value("${spring.redis.port}") private int redisPort;

//    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
//        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());
//        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
//        factory.afterPropertiesSet();
//        return factory;
        return new LettuceConnectionFactory(redisHost, redisPort);
    }
    
    // 가장 많이 조회되고, 값 수정이 적은 user를 캐싱
    @Bean
    public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        // RedisTemplate -> Redis 명령어를 쉽게 작성할 수 있도록 도와주는 클래스 
        RedisTemplate<String, User> redisTemplate =new RedisTemplate<>();
        // setConnectionFactory -> 작성한 명령어를 Redis 서버에 날리기 위해 서버 정보 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value 직렬화
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));

        return  redisTemplate;
    }
}
