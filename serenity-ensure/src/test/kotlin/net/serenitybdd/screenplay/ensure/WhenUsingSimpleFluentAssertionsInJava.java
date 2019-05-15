package net.serenitybdd.screenplay.ensure;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SerenityRunner.class)
/**
 * Some high level smoke tests
 */
public class WhenUsingSimpleFluentAssertionsInJava {

    @Test
    public void weCanMakeAssertionsAboutNumbers() {
        Actor aster = Actor.named("Aster");

        int age = 20;

        aster.attemptsTo(
                Ensure.that(age).isEqualTo(20)
        );
    }


    @Test
    public void weCanMakeAssertionsAboutCollections() {
        Actor aster = Actor.named("Aster");

        List<String> colors = Arrays.asList("red", "green", "blue");

        aster.attemptsTo(
                Ensure.that(colors).isNotNull(),
                Ensure.that("red").isIn(colors)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutEmptyCollections() {
        Actor aster = Actor.named("Aster");

        List<String> nullcolors = null;

        aster.attemptsTo(
                Ensure.that(nullcolors).isNull()
        );
    }

    @Test
    public void weCanMakeAssertionsAboutDoubles() {
        Actor aster = Actor.named("Aster");

        double PI = 3.14195;

        aster.attemptsTo(
                Ensure.that(PI).isGreaterThan(3.0)
        );

    }

    @Test
    public void weCanMakeAssertionsUsingLambdas() {
        Actor aster = Actor.named("Aster");

        String name = "yellow";

        aster.attemptsTo(
                Ensure.that(name).matches("is yellow", color -> color.equals("yellow")),
                Ensure.that(name).not().matches("is red", color -> color.equals("red"))
        );
    }

    @Test
    public void weCanMakeAssertionsAboutStrings() {
        Actor aster = Actor.named("Aster");

        String name = "Bill";
        aster.attemptsTo(
                Ensure.that(name).isEqualToIgnoringCase("BILL")
        );
    }


    @Test
    public void weCanPassNullValues() {
        Actor aster = Actor.named("Aster");

        String nullName = null;

        aster.attemptsTo(
                Ensure.that(nullName).isNullOrEmpty()
        );
    }

    @Test
    public void weCanProvideCustomComparators() {
        Comparator<String> byLength = Comparator.comparingInt(String::length);

        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that("aardvark").usingComparator(byLength).isGreaterThan("cat")
        );
    }

    enum Color {RED, GREEN, BLUE}

    @Test
    public void weCanCompareEnums() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(Color.RED).isLessThan(Color.BLUE),
                Ensure.that(Color.BLUE).usingComparator(Comparator.reverseOrder()).isLessThan(Color.RED)
        );
    }

    @Test
    public void weCanCompareElemetnsInAListAgainstAPredicate() {
        Actor aster = Actor.named("Aster");
        List<String> colors = ImmutableList.of("blue", "cyan", "pink");

        aster.attemptsTo(
                Ensure.that(colors).allMatch("4 characters long", it -> it.length() == 4)
        );

    }

}
