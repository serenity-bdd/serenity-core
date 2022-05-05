package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.model.Requirement;

public class ChildRequirementCounter implements RequirmentCalculator {

    private final Requirement requirement;
    private final TestOutcomes testOutcomes;

    public ChildRequirementCounter(Requirement requirement, TestOutcomes testOutcomes) {
        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
    }


    @Override
    public long countAllSubrequirements() {
        return requirement.getChildren().size();
    }

    @Override
    public long countSubrequirementsWithResult(TestResult expectedResult) {
        return requirement.getChildren().stream()
                .filter(req -> testResultFor(req) == expectedResult)
                .count();
    }

    @Override
    public long countSubrequirementsWithNoTests() {
        return requirement.getChildren().stream()
                .filter(req -> testOutcomes.forRequirement(req).getOutcomes().isEmpty())
                .count();
    }

    private TestResult testResultFor(Requirement req) {

        if (testOutcomes.forRequirement(req).getOutcomes().isEmpty()) {
            return TestResult.UNDEFINED;
        }

        return testOutcomes.forRequirement(req).getResult();
    }
}
