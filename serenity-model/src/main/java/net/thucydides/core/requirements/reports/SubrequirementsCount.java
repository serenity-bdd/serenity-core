package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.model.Requirement;

/**
 * Created by john on 23/07/2016.
 */
public class SubrequirementsCount {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;

    public SubrequirementsCount(Requirement requirement, TestOutcomes testOutcomes) {

        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
    }

    public SubrequirementsProportionCount getProportion() {
        return new SubrequirementsProportionCount(this);
    }

    public SubrequirementsPercentageCount getPercentage() {
        return new SubrequirementsPercentageCount(this);
    }

    public long getTotal() {
        return (requirement.hasChildren()) ?
                usingChildRequirements().countAllSubrequirements() :
                usingAcceptanceCriteria().countAllSubrequirements();
    }

    public long withResult(String resultValue) {
        TestResult result = TestResult.valueOf(resultValue.toUpperCase());

        return (requirement.hasChildren()) ?
                usingChildRequirements().countSubrequirementsWithResult(result) :
                usingAcceptanceCriteria().countSubrequirementsWithResult(result);
    }

    public long withNoTests() {
        return (requirement.hasChildren()) ?
                usingChildRequirements().countSubrequirementsWithNoTests() :
                usingAcceptanceCriteria().countSubrequirementsWithNoTests();
    }

    private RequirmentCalculator usingChildRequirements() {
        return new ChildRequirementCounter(requirement, testOutcomes);
    }

    private RequirmentCalculator usingAcceptanceCriteria() {
        return new AcceptanceCriteriaRequirmentCounter(testOutcomes);
    }

    @Override
    public String toString() {
        return "SubrequirementsCount{" +
                "requirement=" + requirement +
                ", testOutcomes=" + testOutcomes +
                '}';
    }
}
