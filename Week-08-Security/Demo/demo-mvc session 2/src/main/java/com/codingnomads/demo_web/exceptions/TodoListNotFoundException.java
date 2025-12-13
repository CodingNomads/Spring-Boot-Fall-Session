package com.codingnomads.demo_web.exceptions;

public class TodoListNotFoundException extends RuntimeException {
    public TodoListNotFoundException(String message) {
        super(message);
    }
}
