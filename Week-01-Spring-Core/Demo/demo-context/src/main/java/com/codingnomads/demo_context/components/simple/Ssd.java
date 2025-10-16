package com.codingnomads.demo_context.components.simple;

import org.springframework.stereotype.Component;

@Component
public class Ssd {
    @Override
    public String toString() {
        return "Ssd(" + Integer.toHexString(this.hashCode()) + ")";
    }
}
