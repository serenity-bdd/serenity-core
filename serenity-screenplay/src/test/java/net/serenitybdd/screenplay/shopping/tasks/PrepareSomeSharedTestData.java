package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.annotations.Shared;

import static net.serenitybdd.screenplay.Tasks.instrumented;

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
