package com.codingnomads.demo_exception_handling.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hello {
    private String message;
}
