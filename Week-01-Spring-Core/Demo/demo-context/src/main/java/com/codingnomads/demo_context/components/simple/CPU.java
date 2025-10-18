package com.codingnomads.demo_context.components.simple;

import org.springframework.stereotype.Component;

@Component
public class CPU {
    @Override
    public String toString() {
        return "CPU(" + Integer.toHexString(this.hashCode()) + ")";
    }
}
