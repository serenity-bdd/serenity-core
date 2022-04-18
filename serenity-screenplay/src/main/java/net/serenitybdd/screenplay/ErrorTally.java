package net.serenitybdd.screenplay;

import net.serenitybdd.core.Serenity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;

class ErrorTally {

    private final EventBusInterface eventBusInterface;

    private List<FailedConsequence> errors;

    ErrorTally(EventBusInterface eventBusInterface) {
        this.eventBusInterface = eventBusInterface;
        this.errors = new ArrayList<>();
    }

    void recordError(Consequence<?> consequence, Throwable cause) {
        errors.add(new FailedConsequence(consequence, cause));
        eventBusInterface.reportStepFailureFor(consequence, cause);
    }

    void reportAnyErrors() {
        if (errors.isEmpty()) {
            return;
        }
        if (Serenity.shouldThrowErrorsImmediately()) {
            throwSummaryExceptionFrom(errorCausesIn(errors));
        }
    }

    private void throwSummaryExceptionFrom(List<Throwable> errorCauses) {
        String overallErrorMessage = join(System.lineSeparator(), errorMessagesIn(errorCauses));
        throw new AssertionError(overallErrorMessage);
    }

    private List<Throwable> errorCausesIn(List<FailedConsequence> failedConsequences) {
//        return failedConsequences.map(FailedConsequence::getCause);
        return failedConsequences.stream()
                .map(FailedConsequence::getCause)
                .collect(Collectors.toList());
    }

    private List<String> errorMessagesIn(List<Throwable> errorCauses) {
        return errorCauses.stream()
                .map(Throwable::getMessage)
                .collect(Collectors.toList());
//        return errorCauses.map(Throwable::getMessage);
    }
}