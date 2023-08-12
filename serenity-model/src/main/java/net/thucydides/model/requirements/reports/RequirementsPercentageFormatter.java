package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.NumericalFormatter;
import net.thucydides.model.domain.TestResult;

public class RequirementsPercentageFormatter {

        private final RequirementsProportionCounter counter;
        private final NumericalFormatter formatter;

        public RequirementsPercentageFormatter(RequirementsProportionCounter counter) {
            this.counter = counter;
            formatter = new NumericalFormatter();
        }

        public String withResult(String expectedResult) {
            double result = counter.withResult(expectedResult);
            return  formatter.percentage(result, 1);
        }

        public String withResult(TestResult expectedResult) {
            double result = counter.withResult(expectedResult);
            return formatter.percentage(result, 1);
        }

        public String withIndeterminateResult() {
            double result = counter.withIndeterminateResult();
            return formatter.percentage(result, 1);
        }

        public String withSkippedOrIgnored() {
            double result = counter.withResult(TestResult.SKIPPED) + counter.withResult(TestResult.IGNORED);
            return formatter.percentage(result, 1);
        }

        public String withFailureOrError() {
            double result = counter.withResult(TestResult.ERROR)
                    + counter.withResult(TestResult.FAILURE)
                    + counter.withResult(TestResult.COMPROMISED);
            return formatter.percentage(result, 1);
        }


    }
