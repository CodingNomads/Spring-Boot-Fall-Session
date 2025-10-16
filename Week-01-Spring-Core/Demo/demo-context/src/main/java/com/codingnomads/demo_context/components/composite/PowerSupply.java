package com.codingnomads.demo_context.components.composite;

import org.springframework.stereotype.Component;

@Component
public class PowerSupply {
    @Override
    public String toString() {
        return "PowerSupply(" + Integer.toHexString(this.hashCode()) + ")";
    }
}
