package net.serenitybdd.screenplay;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.serenitybdd.core.Serenity;

import java.util.List;

public class ErrorTally {

        private final EventBusInterface eventBusInterface;

        private final List<FailedConsequence> errors;

        public ErrorTally(EventBusInterface eventBusInterface) {
            this.eventBusInterface = eventBusInterface;
            this.errors = Lists.newArrayList();
        }

        public void recordError(Consequence consequence, Throwable cause) {
            errors.add(new FailedConsequence(consequence, cause));
            eventBusInterface.reportStepFailureFor(consequence, cause);
        }

        public void reportAnyErrors() {
            if (errors.isEmpty()) {
                return;
            }
            if (Serenity.shouldThrowErrorsImmediately()) {
                throwSummaryExceptionFrom(errorCausesIn(errors));
            }
        }

    private void throwSummaryExceptionFrom(List<Throwable> errorCauses) {
        String overallErrorMessage = Joiner.on(System.lineSeparator()).join(errorMessagesIn(errorCauses));
        throw new AssertionError(overallErrorMessage);
    }

    private List<Throwable> errorCausesIn(List<FailedConsequence> failedConsequences) {
        List<Throwable> causes = Lists.newArrayList();
        for(FailedConsequence consequence : failedConsequences) {
            causes.add(consequence.getCause());
        }
        return causes;
    }

    private List<String> errorMessagesIn(List<Throwable> errorCauses) {
        List<String> errorMessages = Lists.newArrayList();
        for(Throwable cause : errorCauses) {
            errorMessages.add(cause.getMessage());
        }
        return errorMessages;
    }
}