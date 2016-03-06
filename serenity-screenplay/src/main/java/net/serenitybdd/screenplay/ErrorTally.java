package net.serenitybdd.screenplay;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.serenitybdd.core.Serenity;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

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
                throwSummaryExceptionFrom(extract(errors, on(FailedConsequence.class).getCause()));
            }

        }

    private void throwSummaryExceptionFrom(List<Throwable> errorCauses) {
        List<String> errorMessages = extract(errorCauses, on(Throwable.class).getMessage());
        String overallErrorMessage = Joiner.on(System.lineSeparator()).join(errorMessages);
        throw new AssertionError(overallErrorMessage);
    }


}