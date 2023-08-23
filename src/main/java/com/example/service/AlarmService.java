package com.example.service;

import com.example.exception.ErrorCode;
import com.example.exception.SnsApplicationException;
import com.example.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME="alarm";
    private final EmitterRepository emitterRepository;

    public void send(Integer alarmId, Integer userId){
        emitterRepository.get(userId).ifPresent(sseEmitter -> {
            try{
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e){
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        });
    }

    public SseEmitter connectAlarm(Integer userId){
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try{
            // 이벤트 전송
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect complete"));
        } catch (IOException e){
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;

    }
}
