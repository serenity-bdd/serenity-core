package net.serenitybdd.screenplay;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.StepFactory;

public class Tasks {

    private static Pages pages = new Pages();
    private static StepFactory stepFactory = StepFactory.getFactory().usingPages(pages);

    public static <T extends Performable> T instrumented(Class<T> stepClass, Object... parameters) {
        return stepFactory.getUniqueStepLibraryFor(stepClass, parameters);
    }
}
