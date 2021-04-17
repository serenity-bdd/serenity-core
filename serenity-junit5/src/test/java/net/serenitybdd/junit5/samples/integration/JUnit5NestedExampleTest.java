package net.serenitybdd.junit5.samples.integration;

import net.thucydides.core.annotations.Steps;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;




@DisplayName("JUnit 5 Nested Example")
class JUnit5NestedExampleTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all test methods");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each test method");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all test methods");
    }

    /*@Test
    @DisplayName("Example test for method A")
    void sampleTestForMethodA() {
        System.out.println("Example test for method A");
    }*/

    @Nested
    @DisplayName("Tests for the method A")
    class A {

        @BeforeEach
        void beforeEach() {
            System.out.println("Before each test method of the A class");
        }

        @AfterEach
        public void afterEach() {
            System.out.println("After each test method of the A class");
        }

        @Test
        @DisplayName("Example test for method A")
        void sampleTestForMethodA() {
            System.out.println("Example test for method A");
        }
    }
}