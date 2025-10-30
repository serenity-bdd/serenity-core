package net.serenitybdd.screenplay.examples;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.conditions.Check;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenUsingConditionalPerfomables {

    Apple apple = new Apple();

    @Test
    public void aConditionalWithABooleanExpression() {
        Actor eddie = Actor.named("Eddie");
        EatsTheApple eatsTheApple = new EatsTheApple(apple);

        eddie.attemptsTo(Check.whether(true).andIfSo(eatsTheApple));

        assertThat(apple.isEaten()).isTrue();
    }

    @Test
    public void taskShouldNotBeExecutedIfTheConditionIsNotTrue() {
        Actor eddie = Actor.named("Eddie");
        EatsTheApple eatsTheApple = new EatsTheApple(apple);

        eddie.attemptsTo(Check.whether(false).andIfSo(eatsTheApple));

        assertThat(apple.isEaten()).isFalse();
    }
}
