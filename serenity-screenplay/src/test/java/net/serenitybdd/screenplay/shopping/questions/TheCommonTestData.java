package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.shopping.tasks.CommonData;

public class TheCommonTestData implements Question<Boolean> {
    public static Question<Boolean> initialisationState() {
        return new TheCommonTestData();
    }

    @Steps(shared = true)
    CommonData testData;

    @Override
    public Boolean answeredBy(Actor actor) {
        return testData.initialised;
    }
}
