package net.thucydides.core.steps;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.model.steps.StepListener;

import java.io.File;

public class Listeners {

    public static BaseStepListenerBuilder getBaseStepListener() {
        return new BaseStepListenerBuilder();
    }

    public static class BaseStepListenerBuilder {

        public BaseStepListenerBuilder and() {
            return this;
        }

        public BaseStepListener withOutputDirectory(File outputDirectory) {
            return new BaseStepListener(outputDirectory);
        }
    }

    public static StepListener getLoggingListener() {
        return SerenityInfrastructure.getLoggingListener();
    }
}
