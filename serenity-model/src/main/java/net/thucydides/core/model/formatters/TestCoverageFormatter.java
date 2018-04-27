package net.thucydides.core.model.formatters;

import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;

public class TestCoverageFormatter {

    private final TestOutcomes outcomes;
    private final NumericalFormatter formatter;

    public TestCoverageFormatter(TestOutcomes outcomes) {
        this.outcomes = outcomes;
        formatter = new NumericalFormatter();
    }

    public FormattedPercentageCoverage percentTests(String testType) {
        return percentTests(TestType.valueOf(testType.toUpperCase()));
    }

    public FormattedPercentageCoverage percentTests(TestType testType) {
        return new FormattedPercentageCoverage(testType);
    }

    public FormattedPercentageCoverage percentTests() {
        return new FormattedPercentageCoverage(TestType.ANY);
    }

    public FormattedPercentageCoverage getPercentTests() {
        return percentTests();
    }

    public FormattedPercentageStepCoverage getPercentSteps() {
        return percentSteps();
    }

    public FormattedPercentageStepCoverage percentSteps() {
        return percentSteps(TestType.ANY);
    }

    public FormattedPercentageStepCoverage percentSteps(String testType) {
        return percentSteps(TestType.valueOf(testType.toUpperCase()));
    }

    public FormattedPercentageStepCoverage percentSteps(TestType testType) {
        return new FormattedPercentageStepCoverage(testType);
    }

    public abstract class FormattedCoverage extends TestOutcomeCounter {

        public FormattedCoverage(TestType testType) {
            super(testType);
        }

        public String withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        protected abstract double percentageDeterminedResult();
        protected abstract double percentageWithResult(TestResult expectedResult);

        public String withResult(TestResult expectedResult) {
            return formatter.percentage(percentageWithResult(expectedResult), 1);
        }

        public String withIndeterminateResult() {
            return formatter.percentage(1 - percentageDeterminedResult(), 1);
        }

        public String withFailureOrError() {
            return formatter.percentage(percentageWithResult(TestResult.FAILURE)
                    + percentageWithResult(TestResult.ERROR)
                    + percentageWithResult(TestResult.COMPROMISED), 1);
        }

        public String withSkippedOrIgnored() {
            return formatter.percentage(percentageWithResult(TestResult.SKIPPED) + percentageWithResult(TestResult.IGNORED),1);
        }
    }

    public class FormattedPercentageCoverage extends FormattedCoverage {

        public FormattedPercentageCoverage(TestType testType) {
            super(testType);
        }

        @Override
        protected double percentageDeterminedResult() {
            return outcomes.proportionOf(testType).withResult(TestResult.ERROR)
                    + outcomes.proportionOf(testType).withResult(TestResult.FAILURE)
                    + outcomes.proportionOf(testType).withResult(TestResult.COMPROMISED)
                    + outcomes.proportionOf(testType).withResult(TestResult.SUCCESS);
        }

        @Override
        protected double percentageWithResult(TestResult expectedResult) {
            return outcomes.proportionOf(testType).withResult(expectedResult);
        }


    }

    public class FormattedPercentageStepCoverage extends FormattedCoverage {

        public FormattedPercentageStepCoverage(TestType testType) {
            super(testType);
        }

        @Override
        protected double percentageDeterminedResult() {
            return outcomes.proportionalStepsOf(testType).withResult(TestResult.ERROR)
                    + outcomes.proportionalStepsOf(testType).withResult(TestResult.FAILURE)
                    + outcomes.proportionalStepsOf(testType).withResult(TestResult.COMPROMISED)
                    + outcomes.proportionalStepsOf(testType).withResult(TestResult.SUCCESS);
        }

        @Override
        protected double percentageWithResult(TestResult expectedResult) {
            return outcomes.proportionalStepsOf(testType).withResult(expectedResult);
        }
    }
}

