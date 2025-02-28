package com.example.onlineshopping.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestValidatorUtil {

    public static ResponseEntity<?> getAllErrors(BindingResult bindingResult) {

        Map<String, String> errorMap = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }
}
