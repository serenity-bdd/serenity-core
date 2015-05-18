package net.thucydides.core.requirements.reports;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

public class RequirementOutcome {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;
    private IssueTracking issueTracking;
    private final int requirementsWithoutTests;
    private final int estimatedUnimplementedTests;

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes,
                              int requirementsWithoutTests, int estimatedUnimplementedTests,
                              IssueTracking issueTracking) {
        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
        this.requirementsWithoutTests = requirementsWithoutTests;
        this.estimatedUnimplementedTests = estimatedUnimplementedTests;
        this.issueTracking = issueTracking;
    }

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes, IssueTracking issueTracking) {
        this(requirement, testOutcomes, 0, 0, issueTracking);
    }

    public RequirementOutcome withTestOutcomes(TestOutcomes testOutcomes) {
        return new RequirementOutcome(requirement, testOutcomes, requirementsWithoutTests, estimatedUnimplementedTests, issueTracking);
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public TestOutcomes getTestOutcomes() {
        return testOutcomes;
    }

    /**
     * Is this requirement complete?
     * A Requirement is considered complete if it has associated tests to all of the tests are successful.
     */
    public boolean isComplete() {
        return (!getTestOutcomes().getTests().isEmpty()) && getTestOutcomes().getResult() == TestResult.SUCCESS && allChildRequirementsAreSuccessful();
    }

    public boolean isFailure() {
        return getTestOutcomes().getResult() == TestResult.FAILURE || anyChildRequirementsAreFailures();
    }

    public boolean isError() {
        return getTestOutcomes().getResult() == TestResult.ERROR || anyChildRequirementsAreErrors();
    }


    public boolean isPending() {
        return (getTestOutcomes().getTestCount() == 0) || getTestOutcomes().getResult() == TestResult.PENDING || anyChildRequirementsArePending();
    }

    public boolean isIgnored() {
        return getTestOutcomes().getResult() == TestResult.IGNORED || anyChildRequirementsAreIgnored();
    }

    public int getFlattenedRequirementCount() {
        return getFlattenedRequirements().size();
    }

    public List<Requirement> getFlattenedRequirements() {
        List<Requirement> flattenedRequirements = Lists.newArrayList(requirement);
        flattenedRequirements.addAll(requirement.getNestedChildren());
        return ImmutableList.copyOf(flattenedRequirements);
    }

    public List<Requirement> getFlattenedRequirements(Requirement... excludingRequirement) {
        List<Requirement> flattenedRequirements = Lists.newArrayList(requirement);
        flattenedRequirements.addAll(requirement.getNestedChildren());
        flattenedRequirements.removeAll(Lists.newArrayList(excludingRequirement));
        return ImmutableList.copyOf(flattenedRequirements);
    }

    public int getRequirementsWithoutTestsCount() {
        return requirementsWithoutTests;
    }


    private boolean allChildRequirementsAreSuccessful() {
        if (requirement.hasChildren()) {
            return allChildRequirementsAreSuccessfulFor(requirement.getChildren());
        } else {
            return true;
        }
    }

    private boolean anyChildRequirementsAreFailures() {
        return anyChildRequirementsAreFailuresFor(requirement.getChildren());
    }

    private boolean anyChildRequirementsAreErrors() {
        return anyChildRequirementsAreErrorsFor(requirement.getChildren());
    }
    private boolean anyChildRequirementsArePending() {
        return anyChildRequirementsArePendingFor(requirement.getChildren());
    }

    private boolean anyChildRequirementsAreIgnored() {
        return anyChildRequirementsAreIgnoredFor(requirement.getChildren());
    }

    private boolean allChildRequirementsAreSuccessfulFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                                                                      testOutcomes.forRequirement(requirement),
                                                                      issueTracking);
            if (!childOutcomes.isComplete()) {
                return false;
            } else if (!allChildRequirementsAreSuccessfulFor(childRequirement.getChildren())) {
                return false;
            }
        }
        return true;
    }

    private boolean anyChildRequirementsAreErrorsFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isError()) {
                return true;
            } else if (anyChildRequirementsAreErrorsFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }
    private boolean anyChildRequirementsAreFailuresFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isFailure()) {
                return true;
            } else if (anyChildRequirementsAreFailuresFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }

    private boolean anyChildRequirementsArePendingFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isPending()) {
                return true;
            } else if (anyChildRequirementsArePendingFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }

    private boolean anyChildRequirementsAreIgnoredFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isIgnored()) {
                return true;
            } else if (anyChildRequirementsAreIgnoredFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }


    public String getCardNumberWithLinks() {
        if (requirement.getCardNumber() != null) {
            return getFormatter().addLinks(requirement.getCardNumber());
        } else {
            return "";
        }
    }

    private Formatter getFormatter() {
        return new Formatter(issueTracking);
    }

    @Override
    public String toString() {
        return "RequirementOutcome{" +
                "requirement=" + requirement +
                ", testOutcomes=" + testOutcomes +
                '}';
    }

    public int getTestCount() {
        return testOutcomes.getTotal();
    }

    public TestOutcomes getTests() {
        return testOutcomes;
    }

    public int getEstimatedUnimplementedTests() {
        return estimatedUnimplementedTests;
    }

    private int totalEstimatedAndImplementedTests() {
        int totalImplementedTests = testOutcomes.getTotal();
        return totalImplementedTests + estimatedUnimplementedTests;
    }

    public RequirementsPercentageFormatter getFormattedPercentage() {
        return getFormattedPercentage(TestType.ANY);
    }

    public RequirementsPercentageFormatter getFormattedPercentage(String testType) {
        return new RequirementsPercentageFormatter(percentage(testType));
    }

    public RequirementsPercentageFormatter getFormattedPercentage(TestType testType) {
        return new RequirementsPercentageFormatter(percentage(testType));
    }

    public boolean testsRequirement(Requirement requirement) {
        return requirement.equals(getRequirement()) || testOutcomes.containsTag(requirement.asTag());
    }

    public RequirementsProportionCounter getPercent() {
        return percentage(TestType.ANY);
    }

    public RequirementsProportionCounter percentage(String testType) {
        return percentage(TestType.valueOf(testType.toUpperCase()));
    }

    public RequirementsProportionCounter percentage(TestType testType) {
        return new RequirementsProportionCounter(testType, testOutcomes, totalEstimatedAndImplementedTests());
    }

    public OutcomeCounter getTotal() {
        return new OutcomeCounter(TestType.ANY);
    }

    public OutcomeCounter count(TestType testType) {
        return new OutcomeCounter(testType);
    }

    public OutcomeCounter count(String testType) {
        return new OutcomeCounter(TestType.valueOf(testType.toUpperCase()));
    }

    public Set<String> getReleaseVersions() {
        Set<String> releaseVersions = Sets.newHashSet();
        for(TestOutcome outcome : getTestOutcomes().getOutcomes()) {
            releaseVersions.addAll(outcome.getVersions());
        }
        return ImmutableSet.copyOf(releaseVersions);
    }

    public class OutcomeCounter extends TestOutcomeCounter {

        public OutcomeCounter(TestType testType) {
            super(testType);
        }

        public int withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public int withResult(TestResult expectedResult) {
            return sum(testOutcomes.getOutcomes(), on(TestOutcome.class).countResults(expectedResult, testType));
        }

        public int withIndeterminateResult() {
            return testOutcomes.getTotal() - withResult(TestResult.SUCCESS)
                                           - withResult(TestResult.FAILURE)
                                           - withResult(TestResult.ERROR);
        }

        public int withFailureOrError() {
            return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR);
        }

        public int withAnyResult() {
            return testOutcomes.getTotal();
        }
    }

}
