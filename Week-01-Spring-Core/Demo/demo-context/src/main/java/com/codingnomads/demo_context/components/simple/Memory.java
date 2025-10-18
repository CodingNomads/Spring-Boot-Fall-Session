package com.codingnomads.demo_context.components.simple;

import org.springframework.stereotype.Component;

@Component
public class Memory {
    @Override
    public String toString() {
        return "Memory(" + Integer.toHexString(this.hashCode()) + ")";
    }
}
