package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.reports.TestOutcomes;

public class AcceptanceCriteriaRequirmentCounter implements RequirmentCalculator {

        private final TestOutcomes testOutcomes;

        public AcceptanceCriteriaRequirmentCounter(TestOutcomes testOutcomes) {

            this.testOutcomes = testOutcomes;
        }

        @Override
        public long countAllSubrequirements() {
            return testOutcomes.getOutcomes().size();
        }

        @Override
        public long countSubrequirementsWithResult(TestResult result) {
            return testOutcomes.withResult(result).getOutcomes().size();
        }

        @Override
        public long countSubrequirementsWithNoTests() {
            return testOutcomes.getOutcomes().isEmpty() ? 1 : 0;
        }
    }
