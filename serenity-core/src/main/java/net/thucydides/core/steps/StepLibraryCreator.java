package net.thucydides.core.steps;

import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.STEP_CREATION_STRATEGY;

class StepLibraryCreator {
    static StepLibraryCreationStrategy usingConfiguredCreationStrategy(StepFactory stepFactory,
                                                                       StepsAnnotatedField stepsField,
                                                                       EnvironmentVariables environmentVariables) {
        boolean useDefaultStrategy =  STEP_CREATION_STRATEGY.from(environmentVariables,"default")
                                                            .equalsIgnoreCase("default");

        return (useDefaultStrategy) ?
                new IndividualInstancesByDefaultStepCreationStrategy(stepFactory, stepsField) :
                new SharedInstancesByDefaultStepCreationStrategy(stepFactory, stepsField);
    }
}
