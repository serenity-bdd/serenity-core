package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.Task;
import net.thucydides.core.steps.StepFactory;

import static net.serenitybdd.core.Serenity.getStepFactory;

/**
 * Created by john on 8/08/2015.
 */
public class Tasks {

    private static StepFactory stepFactory = new StepFactory();

    public static <T extends Task> T instrumented(Class<T> purchaseClass) {
        return stepFactory.getUniqueStepLibraryFor(purchaseClass);
    }


}
