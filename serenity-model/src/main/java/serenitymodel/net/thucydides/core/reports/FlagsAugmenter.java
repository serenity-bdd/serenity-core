package serenitymodel.net.thucydides.core.reports;

import serenitymodel.net.thucydides.core.model.TestOutcome;

public class FlagsAugmenter implements OutcomeAugmenter {

        @Override
        public TestOutcome augment(TestOutcome testOutcome) {
            testOutcome.getFlags();
            return testOutcome;
        }
    }
    