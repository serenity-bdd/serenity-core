package net.serenitybdd.screenplay;

import net.thucydides.core.steps.StepEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static net.serenitybdd.screenplay.Actor.ErrorHandlingMode.IGNORE_EXCEPTIONS;

public abstract class BaseConsequence<T> implements Consequence<T> {

    private Class<? extends Error> complaintType;
    private String complaintDetails;
    protected Optional<String> explanation = Optional.empty();
    protected Optional<String> subjectText = Optional.empty();
    private List<Performable> setupActions = new ArrayList<>();

    protected Error errorFrom(Throwable actualError) {
        if (actualError instanceof AssertionError) {
            return null;
        } else if (actualError instanceof Error) {
            return (Error) actualError;
        } else {
            return null;
        }
//        return new Error(actualError);
    }

    protected void throwComplaintTypeErrorIfSpecified(Throwable actualError) {
        if (complaintType != null) {
            throw Complaint.from(complaintType, complaintDetails, actualError);
        }
    }

    protected boolean thisStepShouldBeIgnored() {
        return (StepEventBus.getParallelEventBus().currentTestIsSuspended() || StepEventBus.getParallelEventBus().aStepInTheCurrentTestHasFailed());
    }

    @Override
    public BaseConsequence<T> orComplainWith(Class<? extends Error> complaintType) {
        return orComplainWith(complaintType, null);
    }

    @Override
    public BaseConsequence<T> orComplainWith(Class<? extends Error> complaintType, String complaintDetails) {
        this.complaintType = complaintType;
        this.complaintDetails = complaintDetails;
        return this;
    }

    @Override
    public Consequence<T> whenAttemptingTo(Performable performable) {
        setupActions.add(performable);
        return this;
    }
    @Override
    public Consequence<T> because(String explanation) {
        this.explanation = Optional.ofNullable(explanation);
        return this;
    }

    protected String inputValues() {
        return setupActions.stream()
                .filter(action -> action instanceof RecordsInputs)
                .map(action -> (RecordsInputs) action)
                .map(RecordsInputs::getInputValues)
                .collect(Collectors.joining(","));
    }

    protected String addRecordedInputValuesTo(String message) {
        if (inputValues().isEmpty()) {
            return message;
        }
        return message + " [" + inputValues() + "]";
    }

    public Consequence<T> after(Performable... actions) {
        this.setupActions.addAll(asList(actions));
        return this;
    }

    protected void performSetupActionsAs(Actor actor) {
        actor.attemptsTo(IGNORE_EXCEPTIONS, setupActions.toArray(new Performable[]{}));
    }

}
