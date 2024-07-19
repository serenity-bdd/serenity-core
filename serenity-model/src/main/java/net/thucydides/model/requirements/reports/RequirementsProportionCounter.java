package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestType;
import net.thucydides.model.reports.TestOutcomes;

public class RequirementsProportionCounter {

        private final TestType testType;
        private final TestOutcomes testOutcomes;
        private final long estimatedTotalTests;

        public RequirementsProportionCounter(TestType testType,
                                             TestOutcomes testOutcomes,
                                             long estimatedTotalTests) {
            this.testType = testType;
            this.testOutcomes = testOutcomes;
            this.estimatedTotalTests = estimatedTotalTests;
        }

        public Double withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public Double withResult(TestResult expectedTestResult) {
            long testCount = testOutcomes.count(testType).withResult(expectedTestResult);
            return (estimatedTotalTests == 0) ? 0.0 : ((double) testCount) / ((double) estimatedTotalTests);
        }

        public Double withIndeterminateResult() {
            long passingStepCount = testOutcomes.count(testType).withResult(TestResult.SUCCESS);
            long failingStepCount =  testOutcomes.count(testType).withResult(TestResult.FAILURE);
            long errorStepCount =  testOutcomes.count(testType).withResult(TestResult.ERROR);
            long total = estimatedTotalTests;
            return (total == 0) ? 0.0 : ((total - passingStepCount - failingStepCount - errorStepCount) / (double) total);
        }

    }
