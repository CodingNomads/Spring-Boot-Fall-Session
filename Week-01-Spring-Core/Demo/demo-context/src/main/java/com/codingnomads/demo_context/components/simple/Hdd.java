package com.codingnomads.demo_context.components.simple;

public class Hdd {

    private final String name;

    public Hdd(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hdd " + name + " (" + Integer.toHexString(this.hashCode()) + ")";
    }
}
