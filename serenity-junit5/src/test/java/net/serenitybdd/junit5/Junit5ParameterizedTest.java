package net.serenitybdd.junit5;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Junit5ParameterizedTest {

    @ParameterizedTest
    @ValueSource(strings = { "Hello", "JUnit" ,"5"})
    void withValueSource(String word) {
        assertNotNull(word);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Hello", "JUnit" ,"5"})
    void withValueSourceDoppel(String word) {
        assertNotNull(word);
    }
}
