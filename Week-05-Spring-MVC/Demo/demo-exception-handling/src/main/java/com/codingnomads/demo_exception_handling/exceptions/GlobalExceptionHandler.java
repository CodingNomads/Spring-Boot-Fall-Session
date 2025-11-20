package com.codingnomads.demo_exception_handling.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.codingnomads.demo_exception_handling.controllers.mvc")
public class GlobalExceptionHandler {

    @ExceptionHandler
    public String handleException(Model model, HelloException e) {

        System.out.println("Controller Advice error handling.");

        model.addAttribute("error", e.getMessage());

        e.printStackTrace();
        return "hello";
    }

}
