package serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenitycore.net.thucydides.core.annotations.Steps;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

public class PrepareSomeCommonData implements Task {
    public static Performable inHerTestEnvironment() {
            return instrumented(PrepareSomeCommonData.class);
    }

    @Steps(shared = true)
    CommonData sharedTestData;

    @Override
    public <T extends Actor> void performAs(T actor) {
        sharedTestData.initialised = true;
    }
}
