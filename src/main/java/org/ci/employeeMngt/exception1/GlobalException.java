package org.ci.employeeMngt.exception1;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InspireException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Set the desired HTTP status code
    public ResponseEntity<APIErrorCode> handleInspireException(InspireException e) {
        APIErrorCode errorCode = e.getErrorCode();
        String exData = e.getExData();

        // You can perform additional handling or logging here if needed
        return new ResponseEntity<>(errorCode, HttpStatus.BAD_REQUEST);
    }

}

