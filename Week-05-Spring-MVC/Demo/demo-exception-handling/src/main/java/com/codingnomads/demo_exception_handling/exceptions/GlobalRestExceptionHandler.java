package com.codingnomads.demo_exception_handling.exceptions;

import com.codingnomads.demo_exception_handling.models.HelloError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.codingnomads.demo_exception_handling.controllers.api")
public class GlobalRestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<HelloError> handleException(HelloException e) {

        System.out.println("REST Controller Advice error handling.");

        e.printStackTrace();
        return ResponseEntity.internalServerError().body(HelloError.builder().message(e.getMessage()).build());
    }

}
