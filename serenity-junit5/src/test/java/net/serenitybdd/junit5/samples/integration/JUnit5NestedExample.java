package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;





@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("JUnit 5 Nested Example")
public
class JUnit5NestedExample {

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void beforeEach() {
    }

    @AfterEach
    void afterEach() {
    }

    @AfterAll
    static void afterAll() {
    }

    @Test
    @DisplayName("Example test for method A outerClass")
    void sampleTestForMethodAOuterClass() {
    }

    @Nested
    @DisplayName("Tests for the method A")
    class A {

        @BeforeEach
        void beforeEach() {
        }

        @AfterEach
        public void afterEach() {
        }

        @Test
        @DisplayName("Example test for method A")
        void sampleTestForMethodA() {
        }

        @Test
        void sampleTestForMethodB() {
        }
    }
}