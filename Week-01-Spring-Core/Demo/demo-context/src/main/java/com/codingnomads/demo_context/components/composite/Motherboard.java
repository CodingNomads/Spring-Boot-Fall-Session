package com.codingnomads.demo_context.components.composite;

import com.codingnomads.demo_context.components.simple.CPU;
import com.codingnomads.demo_context.components.simple.Memory;
import org.springframework.stereotype.Component;

@Component
public class Motherboard {
    private final CPU cpu;
    private final Memory memory;

    public Motherboard(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "Motherboard(" + Integer.toHexString(this.hashCode()) + "){" +
                "cpu=" + cpu +
                ", memory=" + memory +
                '}';
    }
}
