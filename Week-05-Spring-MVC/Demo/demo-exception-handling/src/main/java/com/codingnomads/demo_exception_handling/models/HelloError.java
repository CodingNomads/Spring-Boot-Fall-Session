package com.codingnomads.demo_exception_handling.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelloError {
    private String message;
}
