package net.thucydides.core.model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TestTagCache {

    private static class TestOutcomeTag {
        private final TestOutcome testOutcome;
        private final TestTag testTag;

        private TestOutcomeTag(TestOutcome testOutcome, TestTag testTag) {
            this.testOutcome = testOutcome;
            this.testTag = testTag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestOutcomeTag that = (TestOutcomeTag) o;

            if (testOutcome != null ? !testOutcome.equals(that.testOutcome) : that.testOutcome != null) return false;
            return testTag != null ? testTag.equals(that.testTag) : that.testTag == null;
        }

        @Override
        public int hashCode() {
            int result = testOutcome != null ? testOutcome.hashCode() : 0;
            result = 31 * result + (testTag != null ? testTag.hashCode() : 0);
            return result;
        }
    }

    private static final Map<TestOutcomeTag, Boolean> MATCHING_TAGS = new ConcurrentHashMap<>();

    public static Optional<Boolean> hasMatchingTag(TestOutcome outcome, TestTag tag) {
        TestOutcomeTag testOutcomeTag = new TestOutcomeTag(outcome,tag);
        return Optional.ofNullable(MATCHING_TAGS.getOrDefault(testOutcomeTag, null));
    }

    public static void storeMatchingTagResult(TestOutcome outcome, TestTag tag, boolean matchFound) {
        TestOutcomeTag testOutcomeTag = new TestOutcomeTag(outcome,tag);
        MATCHING_TAGS.put(testOutcomeTag, matchFound);
    }

    public static void clear() {
        MATCHING_TAGS.clear();
    }
}
