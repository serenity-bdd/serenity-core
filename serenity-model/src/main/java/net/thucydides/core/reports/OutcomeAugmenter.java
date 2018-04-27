package net.thucydides.core.reports;

import net.thucydides.core.model.TestOutcome;

public interface OutcomeAugmenter {
        TestOutcome augment(TestOutcome testOutcome);
    }