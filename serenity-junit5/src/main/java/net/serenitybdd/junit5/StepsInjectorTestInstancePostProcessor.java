package net.serenitybdd.junit5;

import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class StepsInjectorTestInstancePostProcessor implements TestInstancePostProcessor {

    private StepFactory stepFactory;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        StepFactory stepFactory =  StepFactory.getFactory();
        StepAnnotations.injector().injectScenarioStepsInto(testInstance, stepFactory);
    }
}
