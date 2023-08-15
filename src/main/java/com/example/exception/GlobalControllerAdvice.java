package com.example.exception;

import com.example.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    //@ControllerAdvice를 이용해 오류가 발생했을 때 처리할 로직을 한 곳에 모으고
    //@ExceptionHandler을 이용해 발생한 오류에 대한 세세한 처리

    @ExceptionHandler(SnsApplicationException.class)
    public ResponseEntity<?> applicationHandler(SnsApplicationException e){
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e){
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
    }

}
