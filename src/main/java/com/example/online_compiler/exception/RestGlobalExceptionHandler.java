package com.example.online_compiler.exception;

import com.example.online_compiler.exception.customExceptions.ContainerImageNotFoundException;
import com.example.online_compiler.exception.customExceptions.ContainerNotRunningException;
import com.example.online_compiler.exception.customExceptions.UnableToRunCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("message", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnableToRunCodeException.class)
    public ResponseEntity<?> unableToRunCodeExceptionHandler(UnableToRunCodeException e) {
        return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ContainerNotRunningException.class)
    public ResponseEntity<?> containerNotRunningExceptionHandler(ContainerNotRunningException e) {
        return new ResponseEntity<>(Map.of("message", "Unable to process your request."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ContainerImageNotFoundException.class)
    public ResponseEntity<?> containerImageNotFoundExceptionHandler(ContainerImageNotFoundException e) {
        return new ResponseEntity<>(Map.of("message", "Server under maintenance, please try again after few minutes."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
