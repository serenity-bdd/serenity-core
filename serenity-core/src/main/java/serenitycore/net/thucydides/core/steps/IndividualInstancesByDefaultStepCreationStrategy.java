package serenitycore.net.thucydides.core.steps;

public class IndividualInstancesByDefaultStepCreationStrategy implements StepLibraryCreationStrategy {
    private final StepFactory stepFactory;
    private final StepsAnnotatedField stepsField;

    public IndividualInstancesByDefaultStepCreationStrategy(StepFactory stepFactory, StepsAnnotatedField stepsField) {
        this.stepFactory = stepFactory;
        this.stepsField = stepsField;
    }

    @Override
    public <T> T initiateStepsFor(Class<T> scenarioStepsClass) {
           return (stepsField.isSharedInstance()) ?
                   stepFactory.getSharedStepLibraryFor(scenarioStepsClass) :
                   stepFactory.getNewStepLibraryFor(scenarioStepsClass);
    }
}
