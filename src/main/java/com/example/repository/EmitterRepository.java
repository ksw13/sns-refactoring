package com.example.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class EmitterRepository {

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Integer userId, SseEmitter sseEmitter){
        final String key= getKey(userId);
        emitterMap.put(key, sseEmitter);

        return sseEmitter;
    }

    public Optional<SseEmitter> get(Integer userId){
        final String key= getKey(userId);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(Integer userId){
        final String key= getKey(userId);
        emitterMap.remove(key);

    }


    public String getKey(Integer userId){
        return "Emitter:UID:"+userId;
    }
}
