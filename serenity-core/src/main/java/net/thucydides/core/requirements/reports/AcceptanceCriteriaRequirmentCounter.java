package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomes;

public class AcceptanceCriteriaRequirmentCounter implements RequirmentCalculator {

        private final TestOutcomes testOutcomes;

        public AcceptanceCriteriaRequirmentCounter(TestOutcomes testOutcomes) {

            this.testOutcomes = testOutcomes;
        }

        @Override
        public int countAllSubrequirements() {
            return testOutcomes.getOutcomes().size();
        }

        @Override
        public int countSubrequirementsWithResult(TestResult result) {
            return testOutcomes.withResult(result).getOutcomes().size();
        }

        @Override
        public int countSubrequirementsWithNoTests() {
            return testOutcomes.getOutcomes().isEmpty() ? 1 : 0;
        }
    }