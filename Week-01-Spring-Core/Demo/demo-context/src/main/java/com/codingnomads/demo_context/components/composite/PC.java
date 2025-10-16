package com.codingnomads.demo_context.components.composite;

import org.springframework.stereotype.Component;

@Component
public class PC {

    private final Case pcCase;
    private final Motherboard motherboard;
    private final Storage storage;

    public PC(Case pcCase, Motherboard motherboard, Storage storage) {
        this.pcCase = pcCase;
        this.motherboard = motherboard;
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "PC(" + Integer.toHexString(this.hashCode()) + "){" +
                "pcCase=" + pcCase +
                ", motherboard=" + motherboard +
                ", storage=" + storage +
                '}';
    }
}
