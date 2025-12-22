package com.codingnomads.demo_web.testingDemo;

public class SimpleTestingExample {

    public boolean tryMe(boolean b) {
        return !b;
    }

    public Boolean tryMe(Boolean b, Boolean c) {
        if (b == null && c == null) {
            return null;
        }

        if (b == null) {
            return c;
        }

        if (c == null) {
            return b;
        }

        return b || c;
    }

    public boolean tryMe(int b, int c) {
        return b > 0;
    }

}
