package serenityscreenplay.net.serenitybdd.screenplay.shopping.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks.CommonData;
import serenitycore.net.thucydides.core.annotations.Steps;

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
