package net.thucydides.core.steps;

public class SharedInstancesByDefaultStepCreationStrategy implements StepLibraryCreationStrategy {
    private final StepFactory stepFactory;
    private final StepsAnnotatedField stepsField;

    public SharedInstancesByDefaultStepCreationStrategy(StepFactory stepFactory, StepsAnnotatedField stepsField) {
        this.stepFactory = stepFactory;
        this.stepsField = stepsField;
    }

    @Override
    public <T> T initiateStepsFor(Class<T> scenarioStepsClass) {
        return (stepsField.isUniqueInstance()) ?
                stepFactory.getNewStepLibraryFor(scenarioStepsClass) :
                stepFactory.getSharedStepLibraryFor(scenarioStepsClass);
    }
}
