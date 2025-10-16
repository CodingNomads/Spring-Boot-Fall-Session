package com.codingnomads.demo_context.components.simple;

import org.springframework.stereotype.Component;

@Component
public class Fan {
    @Override
    public String toString() {
        return "Fan(" + Integer.toHexString(this.hashCode()) + ")";
    }
}
