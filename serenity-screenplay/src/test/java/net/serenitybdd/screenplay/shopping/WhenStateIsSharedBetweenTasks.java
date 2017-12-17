package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.shopping.questions.TheCommonTestData;
import net.serenitybdd.screenplay.shopping.questions.TheSharedTestData;
import net.serenitybdd.screenplay.shopping.tasks.PrepareSomeCommonData;
import net.serenitybdd.screenplay.shopping.tasks.PrepareSomeSharedTestData;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class WhenStateIsSharedBetweenTasks {

    Actor dana = Actor.named("Dana");

    @Test
    public void shouldBeAbleToShareStateBetweenTasks() {
        givenThat(dana).wasAbleTo(PrepareSomeCommonData.inHerTestEnvironment());

        then(dana).should(seeThat(TheCommonTestData.initialisationState(),equalTo(true)));
    }

    @Test
    public void shouldBeAbleToShareStateBetweenTasksUsingTheSharedAnnotation() {
        givenThat(dana).wasAbleTo(PrepareSomeSharedTestData.inHerTestEnvironment());

        then(dana).should(seeThat(TheSharedTestData.isInitalised(),equalTo(true)));
    }

    @Test
    public void sharedFieldshouldBeResetForEachTest() {
        givenThat(dana).wasAbleTo(PrepareSomeSharedTestData.inHerTestEnvironment());

        then(dana).should(seeThat(TheSharedTestData.currentCounter(),equalTo(1)));

    }



}

