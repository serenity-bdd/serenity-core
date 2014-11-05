package net.thucydides.junit.runners

import net.thucydides.core.model.TestOutcome

class TestOutcomeChecks {

    static def TestOutcomeChecker resultsFrom(List<TestOutcome> testOutcomes) {
        new TestOutcomeChecker(testOutcomes);
    }

    static class TestOutcomeChecker {
        private final List<TestOutcome> testOutcomes;

        public TestOutcomeChecker(List<TestOutcome> testOutcomes) {
            this.testOutcomes = testOutcomes;
        }

        public TestOutcome getAt(String methodName) {
            def matchingOutcome = testOutcomes.find { it.methodName.equals(methodName) }
            if (!matchingOutcome) {
                throw new AssertionError("No matching test method called $methodName")
            }
            return matchingOutcome
        }
    }
}
