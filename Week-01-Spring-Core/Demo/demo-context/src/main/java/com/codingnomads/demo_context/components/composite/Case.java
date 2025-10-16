package com.codingnomads.demo_context.components.composite;

import com.codingnomads.demo_context.components.simple.Fan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Case {
    // Constructor
    private final Fan fan0;
    // Setter
    private Fan fan1;
    // Field
    @Autowired
    private Fan fan2;

    private final PowerSupply powerSupply;

    public Case(Fan fan0, PowerSupply powerSupply) {
        this.fan0 = fan0;
        this.powerSupply = powerSupply;
    }

    @Autowired
    public void setFan1(Fan fan1) {
        this.fan1 = fan1;
    }

    @Override
    public String toString() {
        return "Case(" + Integer.toHexString(this.hashCode()) + "){" +
                "fan0=" + fan0 +
                ", fan1=" + fan1 +
                ", fan2=" + fan2 +
                ", powerSupply=" + powerSupply +
                '}';
    }
}
