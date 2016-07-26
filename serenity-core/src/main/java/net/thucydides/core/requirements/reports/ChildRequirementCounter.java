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
    public int countAllSubrequirements() {
        return requirement.getChildren().size();
    }

    @Override
    public int countSubrequirementsWithResult(TestResult expectedResult) {
        int totalSubrequirmentsWithResult = 0;
        for (Requirement req : requirement.getChildren()) {
            if (testResultFor(req) == expectedResult) {
                totalSubrequirmentsWithResult++;
            }
        }
        return totalSubrequirmentsWithResult;
    }

    @Override
    public int countSubrequirementsWithNoTests() {
        int totalSubrequirmentsWithNoTests = 0;
        for (Requirement req : requirement.getChildren()) {
            if (testOutcomes.forRequirement(req).getOutcomes().isEmpty()) {
                totalSubrequirmentsWithNoTests++;
            }
        }
        return totalSubrequirmentsWithNoTests;
    }

    private TestResult testResultFor(Requirement req) {

        if (testOutcomes.forRequirement(req).getOutcomes().isEmpty()) {
            return TestResult.UNDEFINED;
        }

        return testOutcomes.forRequirement(req).getResult();
    }
}
