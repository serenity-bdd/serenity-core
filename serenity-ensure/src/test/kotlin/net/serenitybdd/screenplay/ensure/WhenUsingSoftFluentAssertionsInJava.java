package net.serenitybdd.screenplay.ensure;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.ensure.web.NamedExpectation;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.containsString;

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
