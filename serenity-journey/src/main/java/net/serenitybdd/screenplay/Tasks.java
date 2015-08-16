package net.serenitybdd.screenplay;

import net.thucydides.core.steps.StepFactory;

public class Tasks {

    private static StepFactory stepFactory = new StepFactory();

    public static <T extends Performable> T instrumented(Class<T> purchaseClass) {
        return stepFactory.getUniqueStepLibraryFor(purchaseClass);
    }
}
