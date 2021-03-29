package serenitymodel.net.thucydides.core.reports;

import serenitymodel.net.thucydides.core.model.TestOutcome;

public interface OutcomeAugmenter {
        TestOutcome augment(TestOutcome testOutcome);
    }