package net.thucydides.model.domain;

import net.thucydides.model.domain.failures.AssertionErrorMessagesAggregator;

import java.util.List;

public class MultipleAssertionErrors extends AssertionError {
    private final List<String> errors;

    public MultipleAssertionErrors(List<String> errors) {
        super(AssertionErrorMessagesAggregator.aggregateErrorMessages(errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
