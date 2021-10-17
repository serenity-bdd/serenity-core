package net.serenitybdd.junit5;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class StepsInjectorTestInstancePostProcessor implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
//        StepFactory stepFactory =  StepFactory.getFactory();
//        StepAnnotations.injector().injectScenarioStepsInto(testInstance, stepFactory);
        Serenity.initialize(testInstance);
    }
}
