package net.thucydides.model.reports;

import net.thucydides.model.domain.TestOutcome;

public interface OutcomeAugmenter {
        TestOutcome augment(TestOutcome testOutcome);
    }
