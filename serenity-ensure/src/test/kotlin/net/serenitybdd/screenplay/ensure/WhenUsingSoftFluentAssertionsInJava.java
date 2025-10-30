package net.serenitybdd.screenplay.ensure;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SerenityRunner.class)
/**
 * Some high level smoke tests
 */
public class WhenUsingSoftFluentAssertionsInJava {

    Actor aster = Actor.named("Aster");

    @Before
    public void prepareSoftAsserts() {
        Ensure.enableSoftAssertions();
    }

    @After
    public void reportSoftAsserts() {
        Ensure.reportSoftAssertions();
    }

    @Test
    public void aTestWithSomeSoftAsserts() {

        int age = 20;

        aster.attemptsTo(
                Ensure.that(age).isEqualTo(20)
        );
    }

    @Test
    public void anotherTestWithSomeSoftAsserts() {
        Actor aster = Actor.named("Aster");

        String color = "red";

        aster.attemptsTo(
                Ensure.that(color).startsWith("r"),
                Ensure.that(color).endsWith("d"),
                Ensure.that(color).hasSize(3)
        );
    }

}
