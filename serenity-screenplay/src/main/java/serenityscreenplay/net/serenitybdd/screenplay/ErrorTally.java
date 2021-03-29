package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.core.Serenity;

import static io.vavr.API.List;
import io.vavr.collection.List;
import static java.lang.String.join;

class ErrorTally {

    private final EventBusInterface eventBusInterface;

    private List<FailedConsequence> errors;

    ErrorTally(EventBusInterface eventBusInterface) {
        this.eventBusInterface = eventBusInterface;
        this.errors = List();
    }

    void recordError(Consequence<?> consequence, Throwable cause) {
        errors = errors.append(new FailedConsequence(consequence, cause));
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
        return failedConsequences.map(FailedConsequence::getCause);
    }

    private List<String> errorMessagesIn(List<Throwable> errorCauses) {
        return errorCauses.map(Throwable::getMessage);
    }
}