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

        int precision = 1;

        public FormattedCoverage(TestType testType) {
            super(testType);
        }

        public FormattedCoverage withPrecision(int precision) {
            this.precision = precision;
            return this;
        }

        public String withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        protected abstract double percentageDeterminedResult();

        protected abstract double percentageWithResult(TestResult expectedResult);

        public String withResult(TestResult expectedResult) {
            return withResult(expectedResult, precision);
        }

        public String withResult(TestResult expectedResult, int precision) {
            return formatter.percentage(percentageWithResult(expectedResult), precision);
        }

        public String withIndeterminateResult() {
            return formatter.percentage(1 - percentageDeterminedResult(), precision);
        }

        public String withPass() {
            return formatter.percentage(percentageWithResult(TestResult.SUCCESS), precision);
        }

        public String withFailureOrError() {
            return formatter.percentage(percentageWithResult(TestResult.FAILURE)
                    + percentageWithResult(TestResult.ERROR)
                    + percentageWithResult(TestResult.ABORTED)
                    + percentageWithResult(TestResult.COMPROMISED), precision);
        }

        public String withSkippedOrIgnored() {
            return formatter.percentage(percentageWithResult(TestResult.SKIPPED)
                            + percentageWithResult(TestResult.IGNORED),
                    precision);
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

        protected double percentageWithResult(String expectedResult) {
            return percentageWithResult(TestResult.valueOf(expectedResult.toUpperCase()));
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

