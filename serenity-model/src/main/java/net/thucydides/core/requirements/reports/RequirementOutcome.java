package net.thucydides.core.requirements.reports;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.RequirementsFilter;
import net.thucydides.core.requirements.ExcludedUnrelatedRequirementTypes;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RequirementOutcome {
    private final Requirement requirement;
    private final TestOutcomes testOutcomes;
    private IssueTracking issueTracking;
    private final long requirementsWithoutTests;
    private final long estimatedUnimplementedTests;
    private final EnvironmentVariables environmentVariables;
    private ReportFormatter reportFormatter;

    public RequirementOutcome(Requirement requirement, TestOutcomes testOutcomes,
                              long requirementsWithoutTests, long estimatedUnimplementedTests,
                              IssueTracking issueTracking) {
        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
        this.requirementsWithoutTests = requirementsWithoutTests;
        this.estimatedUnimplementedTests = estimatedUnimplementedTests;
        this.issueTracking = issueTracking;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
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

    public boolean isCompromised() {
        return getTestOutcomes().getResult() == TestResult.COMPROMISED || anyChildRequirementsAreCompromised();
    }

    public boolean isPending() {
//        return (getTestOutcomes().getTestCount() == 0) || getTestOutcomes().getResult() == TestResult.PENDING || anyChildRequirementsArePending();
        return getTestOutcomes().getResult() == TestResult.PENDING || anyChildRequirementsArePending();
    }

    public boolean isIgnored() {
        return getTestOutcomes().getResult() == TestResult.IGNORED || anyChildRequirementsAreIgnored();
    }

    public boolean isSkipped() {
        return getTestOutcomes().getResult() == TestResult.SKIPPED || anyChildRequirementsAreSkipped();
    }

    public int getFlattenedRequirementCount() {
        return getFlattenedRequirements().size();
    }

    public List<Requirement> getFlattenedRequirements() {
        List<Requirement> flattenedRequirements = new ArrayList();
        flattenedRequirements.add(requirement);
        flattenedRequirements.addAll(requirement.getNestedChildren());
        return NewList.copyOf(flattenedRequirements);
    }

    public List<Requirement> getFlattenedRequirements(Requirement... excludingRequirement) {
        List<Requirement> flattenedRequirements = new ArrayList();
        flattenedRequirements.add(requirement);
        flattenedRequirements.addAll(requirement.getNestedChildren());
        flattenedRequirements.removeAll(NewList.of(excludingRequirement));
        return NewList.copyOf(flattenedRequirements);
    }

    public long getRequirementsWithoutTestsCount() {
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

    private boolean anyChildRequirementsAreCompromised() {
        return anyChildRequirementsAreCompromisedFor(requirement.getChildren());
    }

    private boolean anyChildRequirementsArePending() {
        return anyChildRequirementsArePendingFor(requirement.getChildren());
    }

    private boolean anyChildRequirementsAreIgnored() {
        return anyChildRequirementsAreIgnoredFor(requirement.getChildren());
    }

    private boolean anyChildRequirementsAreSkipped() {
        return anyChildRequirementsAreSkippedFor(requirement.getChildren());
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

    private boolean anyChildRequirementsAreCompromisedFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isCompromised()) {
                return true;
            } else if (anyChildRequirementsAreErrorsFor(childRequirement.getChildren())) {
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

    private boolean anyChildRequirementsAreSkippedFor(List<Requirement> requirements) {
        for(Requirement childRequirement : requirements) {
            RequirementOutcome childOutcomes = new RequirementOutcome(childRequirement,
                    testOutcomes.forRequirement(requirement), issueTracking);
            if (childOutcomes.isSkipped()) {
                return true;
            } else if (anyChildRequirementsAreIgnoredFor(childRequirement.getChildren())) {
                return true;
            }
        }
        return false;
    }

    public String getCardNumberWithLinks() {
        if (requirement.getCardNumber() != null) {
            return new ReportFormatter(issueTracking).addLinks(requirement.getCardNumber());
        } else {
            return "";
        }
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

    public long getEstimatedUnimplementedTests() {
        return estimatedUnimplementedTests;
    }

    private long totalEstimatedAndImplementedTests() {
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

    public PercentageRequirementCounter getPercentRequirements() {
        return new PercentageRequirementCounter(requirement, testOutcomes);
    }

    public SubrequirementsCount getSubrequirements() {
        return new SubrequirementsCount(requirement, testOutcomes);
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
        Set<String> releaseVersions = new HashSet();
        for(TestOutcome outcome : getTestOutcomes().getOutcomes()) {
            releaseVersions.addAll(outcome.getVersions());
        }
        return NewSet.copyOf(releaseVersions);
    }

    public RequirementOutcome withoutUnrelatedRequirements() {

        if (!shouldPrune(this)) {
            return this;
        }

        List<Requirement> childRequirementsWithTests = getRequirement().getChildren().stream()
                .filter(this::isTested)
                .collect(Collectors.toList());

        Requirement prunedRequirement = getRequirement().withChildren(childRequirementsWithTests);
        return new RequirementOutcome(prunedRequirement, testOutcomes, requirementsWithoutTests, estimatedUnimplementedTests, issueTracking);
    }

    public RequirementOutcome filteredByDisplayTag() {

        List<Requirement> childRequirementsWithMatchingTag = getRequirement().getChildren().stream()
                .filter(this::isFilteredByDisplayTag)
                .collect(Collectors.toList());

        Requirement filteredRequirement = getRequirement().withChildren(childRequirementsWithMatchingTag);
        return new RequirementOutcome(filteredRequirement, testOutcomes, requirementsWithoutTests, estimatedUnimplementedTests, issueTracking);

    }

    public boolean shouldPrune(RequirementOutcome requirementOutcome) {
        return ExcludedUnrelatedRequirementTypes.definedIn(environmentVariables)
                .excludeUntestedChildrenOfRequirementOfType(requirementOutcome.getRequirement().getType());
    }


    private boolean isTested(Requirement childRequirement) {
        return !testOutcomes.forRequirement(childRequirement).getOutcomes().isEmpty();
    }

    private boolean isFilteredByDisplayTag(Requirement childRequirement) {
        RequirementsFilter requirementsFilter = new RequirementsFilter(environmentVariables);
        return !requirementsFilter.inDisplayOnlyTags(childRequirement);
    }

    public class OutcomeCounter extends TestOutcomeCounter {

        public OutcomeCounter(TestType testType) {
            super(testType);
        }

        public int withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public int withResult(TestResult expectedResult) {
            return testOutcomes.getOutcomes().stream()
                    .mapToInt(outcome -> outcome.countResults(expectedResult, testType))
                    .sum();
        }

        public int withIndeterminateResult() {
            return testOutcomes.getTotal() - withResult(TestResult.SUCCESS)
                                           - withResult(TestResult.FAILURE)
                                           - withResult(TestResult.ERROR);
        }

        public int withFailureOrError() {
            return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR) + withResult(TestResult.COMPROMISED);
        }

        public int withAnyResult() {
            return testOutcomes.getTotal();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequirementOutcome that = (RequirementOutcome) o;

        return requirement != null ? requirement.equals(that.requirement) : that.requirement == null;
    }

    @Override
    public int hashCode() {
        return requirement != null ? requirement.hashCode() : 0;
    }
}
