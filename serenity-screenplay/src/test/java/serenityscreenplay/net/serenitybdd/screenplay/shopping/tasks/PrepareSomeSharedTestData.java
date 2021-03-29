package serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenitycore.net.thucydides.core.annotations.Shared;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

public class PrepareSomeSharedTestData implements Task {
    @Shared
    TestData sharedTestData;

    public static Performable inHerTestEnvironment() {
        return instrumented(PrepareSomeSharedTestData.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        sharedTestData.initialised = true;
        sharedTestData.counter++;
    }
}
