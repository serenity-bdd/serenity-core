package net.serenitybdd.screenplay.shopping.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.annotations.Steps;

import static net.serenitybdd.screenplay.Tasks.instrumented;

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
