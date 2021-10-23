package net.serenitybdd.junit5.samples.integration;

import net.serenitybdd.junit5.SerenityBDD;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;




@ExtendWith(SerenityJUnit5Extension.class)
@SerenityBDD
@DisplayName("JUnit 5 Nested Example")
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