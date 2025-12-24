package com.codingnomads.demo_web.testingDemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTestingExampleTest {

    // --- tryMe(boolean) ---
    @Test
    void tryMe_false() {
        SimpleTestingExample simpleTestingExample = new SimpleTestingExample();
        boolean result = simpleTestingExample.tryMe(false);
        assertTrue(result);
    }

    @Test
    void tryMe_true() {
        SimpleTestingExample simpleTestingExample = new SimpleTestingExample();
        boolean result = simpleTestingExample.tryMe(true);
        assertFalse(result);
    }

    // --- tryMe(Boolean, Boolean) ---
    @DisplayName("tryMe(Boolean, Boolean) all permutations including nulls")
    @ParameterizedTest(name = "b={0}, c={1} -> expected={2}")
    @MethodSource("booleanBoxedPermutations")
    void tryMe_twoParams_allPermutations(Boolean b, Boolean c, Boolean expected) {
        SimpleTestingExample simpleTestingExample = new SimpleTestingExample();
        Boolean result = simpleTestingExample.tryMe(b, c);
        if (expected == null) {
            assertNull(result);
        } else {
            assertEquals(expected, result);
        }
    }

    static Stream<Arguments> booleanBoxedPermutations() {
        return Stream.of(
                Arguments.of((Boolean) null, (Boolean) null, (Boolean) null),
                Arguments.of((Boolean) null, Boolean.TRUE, Boolean.TRUE),
                Arguments.of((Boolean) null, Boolean.FALSE, Boolean.FALSE),
                Arguments.of(Boolean.TRUE, (Boolean) null, Boolean.TRUE),
                Arguments.of(Boolean.FALSE, (Boolean) null, Boolean.FALSE),
                Arguments.of(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE),
                Arguments.of(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE),
                Arguments.of(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE),
                Arguments.of(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
        );
    }

    // --- tryMe(int, int) ---
    @DisplayName("tryMe(int, int) depends only on first argument b > 0")
    @ParameterizedTest(name = "b={0}, c={1} -> expected={2}")
    @CsvSource({
            "-5, 0, false",
            "-1, 10, false",
            "0, 0, false",
            "0, 99, false",
            "1, 0, true",
            "2, -3, true",
            "10, 100, true"
    })
    void tryMe_intParams_various(int b, int c, boolean expected) {
        SimpleTestingExample simpleTestingExample = new SimpleTestingExample();
        boolean result = simpleTestingExample.tryMe(b, c);
        assertEquals(expected, result);
    }
}