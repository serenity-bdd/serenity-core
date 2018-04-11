package net.thucydides.core.requirements;

import net.serenitybdd.core.collect.NewList;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by john on 22/07/2015.
 */
public class SerenityTestCaseFinder {
    private final List<String> LEGAL_SERENITY_RUNNER_NAMES = NewList.of("SerenityRunner", "ThucydidesRunner");

    public boolean isSerenityTestCase(Class<?> testClass) {
        RunWith runWithAnnotation = testClass.getAnnotation(RunWith.class);
        if (runWithAnnotation != null) {
            return LEGAL_SERENITY_RUNNER_NAMES.contains(runWithAnnotation.value().getSimpleName());
        }
        return false;
    }


}
