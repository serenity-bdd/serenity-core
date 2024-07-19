package net.thucydides.model.reports;

import net.thucydides.model.domain.TestOutcome;

public class FlagsAugmenter implements OutcomeAugmenter {

        @Override
        public TestOutcome augment(TestOutcome testOutcome) {
            testOutcome.getFlags();
            return testOutcome;
        }
    }
