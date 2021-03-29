package serenitycore.net.thucydides.core.steps;

public interface StepLibraryCreationStrategy {
    <T> T initiateStepsFor(Class<T> scenarioStepsClass);
}
