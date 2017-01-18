package net.thucydides.core.requirements.reports;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.OutcomeCounter;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestType;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.ExcludedUnrelatedRequirementTypes;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.Matchers.hasItem;

/**
 * A set of test results for a list of high-level requirements.
 */
public class RequirementsOutcomes {
    private final List<RequirementOutcome> requirementOutcomes;
    private final TestOutcomes testOutcomes;
    private final Optional<Requirement> parentRequirement;
    private final EnvironmentVariables environmentVariables;
    private final IssueTracking issueTracking;
    private final List<? extends RequirementsTagProvider> requirementsTagProviders;
    private final ReleaseManager releaseManager;
    private final ReportNameProvider reportNameProvider;
    List<RequirementOutcome> flattenedRequirementOutcomes = null;

    public final static Integer DEFAULT_TESTS_PER_REQUIREMENT = 4;

    private final Map<String, Integer> totalCountCache = new ConcurrentHashMap<>();

    public RequirementsOutcomes(List<Requirement> requirements,
                                TestOutcomes testOutcomes,
                                IssueTracking issueTracking,
                                EnvironmentVariables environmentVariables,
                                List<? extends RequirementsTagProvider> requirementsTagProviders,
                                ReportNameProvider reportNameProvider) {
        this(null, requirements, testOutcomes, issueTracking, environmentVariables, requirementsTagProviders, reportNameProvider);
    }

    public RequirementsOutcomes(Requirement parentRequirement, List<Requirement> requirements, TestOutcomes testOutcomes,
                                IssueTracking issueTracking, EnvironmentVariables environmentVariables,
                                List<? extends RequirementsTagProvider> requirementsTagProviders,
                                ReportNameProvider reportNameProvider) {
        this.testOutcomes = testOutcomes;
        this.parentRequirement = Optional.fromNullable(parentRequirement);
        this.environmentVariables = environmentVariables;
        this.issueTracking = issueTracking;
        this.requirementsTagProviders = requirementsTagProviders;
        this.requirementOutcomes = buildRequirementOutcomes(requirements);
        this.reportNameProvider = reportNameProvider;
        this.releaseManager = new ReleaseManager(environmentVariables, reportNameProvider);
    }

    RequirementsOutcomes(ReportNameProvider reportNameProvider, List<RequirementOutcome> requirementOutcomes, TestOutcomes testOutcomes, Optional<Requirement> parentRequirement, EnvironmentVariables environmentVariables, IssueTracking issueTracking, List<? extends RequirementsTagProvider> requirementsTagProviders, ReleaseManager releaseManager) {
        this.reportNameProvider = reportNameProvider;
        this.requirementOutcomes = requirementOutcomes;
        this.testOutcomes = testOutcomes;
        this.parentRequirement = parentRequirement;
        this.environmentVariables = environmentVariables;
        this.issueTracking = issueTracking;
        this.requirementsTagProviders = requirementsTagProviders;
        this.releaseManager = releaseManager;
    }

    private List<RequirementOutcome> buildRequirementOutcomes(List<Requirement> requirements) {
        List<RequirementOutcome> outcomes = Lists.newArrayList();
        for (Requirement requirement : requirements) {
            buildRequirements(outcomes, requirement);
        }
        return outcomes;
    }

    RequirementsOutcomesOfTypeCache requirementsOfTypeCache = new RequirementsOutcomesOfTypeCache(this);

    public RequirementsOutcomes requirementsOfType(String type) {
        return requirementsOfTypeCache.byType(type);
    }

    public RequirementsOutcomes ofType(String type) {
        List<Requirement> matchingRequirements = Lists.newArrayList();
        List<TestOutcome> matchingTests = Lists.newArrayList();
        for (RequirementOutcome requirementOutcome : getFlattenedRequirementOutcomes()) {
            if (requirementOutcome.getRequirement().getType().equalsIgnoreCase(type)) {
                matchingRequirements.add(requirementOutcome.getRequirement());
                matchingTests.addAll(requirementOutcome.getTestOutcomes().getOutcomes());
            }
        }

        return new RequirementsOutcomes(matchingRequirements,
                TestOutcomes.of(matchingTests),
                issueTracking,
                environmentVariables,
                requirementsTagProviders,
                reportNameProvider).withoutUnrelatedRequirements();
    }

    private void buildRequirements(List<RequirementOutcome> outcomes, Requirement requirement) {
        TestOutcomes outcomesForRequirement = testOutcomes.forRequirement(requirement);

        int requirementsWithoutTests = countRequirementsWithoutTestsIn(requirement);
        int estimatedUnimplementedTests = requirementsWithoutTests * estimatedTestsPerRequirement();
        outcomes.add(new RequirementOutcome(requirement, outcomesForRequirement, requirementsWithoutTests,
                estimatedUnimplementedTests, issueTracking));
    }

    private int countRequirementsWithoutTestsIn(Requirement rootRequirement) {
        List<Requirement> flattenedRequirements = getFlattenedRequirements(rootRequirement);

        int requirementsWithoutTests = 0;
        for (Requirement requirement : flattenedRequirements) {
            TestOutcomes matchingOutcomes = testOutcomes.forRequirement(requirement);
            if (matchingOutcomes.getTotal() == 0) {
                requirementsWithoutTests++;
            }
        }
        return requirementsWithoutTests;
    }

    public int getFlattenedRequirementCount() {
        if (totalIsCachedFor("FlattenedRequirementCount")) { return cachedTotalOf("FlattenedRequirementCount"); }

        int requirementCount = 0;
        for (RequirementOutcome requirement : requirementOutcomes) {
            requirementCount += requirement.getFlattenedRequirementCount();

        }
        return cachedTotal("FlattenedRequirementCount", requirementCount);
    }

    private int cachedTotal(String key, int total) {
        totalCountCache.put(key, total);
        return total;
    }

    private int cachedTotalOf(String key) {
        return totalCountCache.get(key);
    }

    private boolean totalIsCachedFor(String key) { return totalCountCache.containsKey(key); }

    private List<Requirement> getFlattenedRequirements(Requirement rootRequirement) {
        List<Requirement> flattenedRequirements = Lists.newArrayList();
        flattenedRequirements.add(rootRequirement);
        flattenedRequirements.addAll(rootRequirement.getNestedChildren());
        return flattenedRequirements;
    }

    public Optional<Requirement> getParentRequirement() {
        return parentRequirement;
    }

    public Optional<Requirement> getGrandparentRequirement() {
        if (!parentRequirement.isPresent()) { return Optional.absent(); }
        if (isEmpty(parentRequirement.get().getParent())) { return Optional.absent(); }

        return parentRequirementOf(parentRequirement.get());
    }

    private Optional<Requirement> parentRequirementOf(Requirement requirement) {
        for (RequirementsTagProvider tagProvider : this.requirementsTagProviders) {
            if (tagProvider.getParentRequirementOf(requirement).isPresent()) {
                return tagProvider.getParentRequirementOf(requirement);
            }
        }
        return Optional.absent();
    }

    public int getRequirementCount() {
        return requirementOutcomes.size();
    }

    public List<RequirementOutcome> getRequirementOutcomes() {
        return ImmutableList.copyOf(requirementOutcomes);
    }

    public String getType() {
        if (requirementOutcomes.isEmpty()) {
            return "requirement";
        } else {
            return requirementOutcomes.get(0).getRequirement().getType();
        }
    }

    public String getChildrenType() {
        return typeOfFirstChildPresent();
    }

    public List<String> getTypes() {
        List<Requirement> requirements = getAllRequirements();
        List<String> types = new ArrayList<>();
        for (Requirement requirement : requirements) {
            if (!types.contains(requirement.getType())) {
                types.add(requirement.getType());
            }
        }
        return types;
    }

    private String typeOfFirstChildPresent() {
        for (RequirementOutcome outcome : requirementOutcomes) {
            if (!outcome.getRequirement().getChildren().isEmpty()) {
                Requirement firstChildRequirement = outcome.getRequirement().getChildren().get(0);
                return firstChildRequirement.getType();
            }
        }
        return null;
    }

    public TestOutcomes getTestOutcomes() {
        return testOutcomes;
    }

    @Override
    public String toString() {
        return "RequirementsOutcomes{" +
                "requirementOutcomes=" + requirementOutcomes +
                ", parentRequirement=" + parentRequirement +
                '}';
    }

    public int getCompletedRequirementsCount() {
        if (totalIsCachedFor("CompletedRequirementsCount")) { return cachedTotalOf("CompletedRequirementsCount"); }

        int completedRequirements = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isComplete()) {
                completedRequirements++;
            }
        }
        return cachedTotal("CompletedRequirementsCount", completedRequirements);
    }

    public int getUnsuccessfulRequirementsCount() {
        return getErrorRequirementsCount() + getFailingRequirementsCount() + getCompromisedRequirementsCount();
    }

    public int getErrorRequirementsCount() {
        if (totalIsCachedFor("ErrorRequirementsCount")) { return cachedTotalOf("ErrorRequirementsCount"); }

        int failingRequirements = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isError()) {
                failingRequirements++;
            }
        }
        return cachedTotal("ErrorRequirementsCount", failingRequirements);
    }

    public int getFailingRequirementsCount() {
        if (totalIsCachedFor("FailingRequirementsCount")) { return cachedTotalOf("FailingRequirementsCount"); }

        int failingRequirements = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isFailure()) {
                failingRequirements++;
            }
        }
        return cachedTotal("FailingRequirementsCount", failingRequirements);
    }

    public int getPendingRequirementsCount() {
        if (totalIsCachedFor("PendingRequirementsCount")) { return cachedTotalOf("PendingRequirementsCount"); }

        int total = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isPending()) {
                total++;
            }
        }
        return cachedTotal("PendingRequirementsCount", total);
    }

    public int getCompromisedRequirementsCount() {
        if (totalIsCachedFor("CompromisedRequirementsCount")) { return cachedTotalOf("CompromisedRequirementsCount"); }

        int total = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isCompromised()) {
                total++;
            }
        }
        return cachedTotal("CompromisedRequirementsCount", total);
    }

    public int getIgnoredRequirementsCount() {
        if (totalIsCachedFor("IgnoredRequirementsCount")) { return cachedTotalOf("IgnoredRequirementsCount"); }

        int total = 0;
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.isIgnored()) {
                total++;
            }
        }
        return cachedTotal("IgnoredRequirementsCount", total);
    }

    public int getRequirementsWithoutTestsCount() {
        if (totalIsCachedFor("RequirementsWithoutTestsCount")) { return cachedTotalOf("RequirementsWithoutTestsCount"); }

        int requirementsWithNoTests = 0;
        for (Requirement requirement : getTopLevelRequirements()) {
            if (!testsRecordedFor(requirement) && !isPending(requirement)) {
                requirementsWithNoTests++;
            }
        }
        return cachedTotal("IgnoredRequirementsCount", requirementsWithNoTests);
    }

    private boolean isPending(Requirement requirement) {
        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (requirementOutcome.getRequirement().equals(requirement) && requirementOutcome.isPending()) {
                return true;
            }
        }
        return false;
    }

    private boolean testsRecordedFor(Requirement requirement) {
        for (RequirementOutcome outcome : requirementOutcomes) {
            if (outcome.testsRequirement(requirement) && outcome.getTestCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private List<Requirement> getAllRequirements() {
        List<Requirement> allRequirements = Lists.newArrayList();
        for (RequirementOutcome outcome : requirementOutcomes) {
            addFlattenedRequirements(outcome.getRequirement(), allRequirements);
        }
        return allRequirements;
    }

    private List<Requirement> getTopLevelRequirements() {
        List<Requirement> requirements = Lists.newArrayList();
        for (RequirementOutcome outcome : requirementOutcomes) {
            requirements.add(outcome.getRequirement());
        }
        return requirements;
    }

    public int getTotalRequirements() {
        return getAllRequirements().size();
    }

    private void addFlattenedRequirements(Requirement requirement, List<Requirement> allRequirements) {
        allRequirements.add(requirement);
        for (Requirement child : requirement.getChildren()) {
            addFlattenedRequirements(child, allRequirements);
        }
    }

    public List<RequirementOutcome> getFlattenedRequirementOutcomes() {
        if (flattenedRequirementOutcomes == null) {
            flattenedRequirementOutcomes = getFlattenedRequirementOutcomes(requirementOutcomes);
        }
        return flattenedRequirementOutcomes;
    }

    public List<RequirementOutcome> getFlattenedRequirementOutcomes(List<RequirementOutcome> outcomes) {
        Set<RequirementOutcome> flattenedOutcomes = Sets.newHashSet();

        for (RequirementOutcome requirementOutcome : outcomes) {
            flattenedOutcomes.add(requirementOutcome);
            for (Requirement requirement : requirementOutcome.getRequirement().getChildren()) {

                TestOutcomes testOutcomesForRequirement = requirementOutcome.getTestOutcomes().forRequirement(requirement);

                flattenedOutcomes.add(new RequirementOutcome(requirement, testOutcomesForRequirement, issueTracking));

                List<Requirement> childRequirements = requirement.getChildren();
                RequirementsOutcomes childOutcomes =
                        new RequirementsOutcomes(childRequirements, testOutcomesForRequirement, issueTracking,
                                environmentVariables, requirementsTagProviders, reportNameProvider).withoutUnrelatedRequirements();
                flattenedOutcomes.addAll(getFlattenedRequirementOutcomes(childOutcomes.getRequirementOutcomes()));
            }
        }

        return ImmutableList.copyOf(flattenedOutcomes);
    }

    public OutcomeCounter getTotal() {
        return count(TestType.ANY);
    }

    public OutcomeCounter count(TestType testType) {
        return new OutcomeCounter(testType, getTestOutcomes());
    }

    public OutcomeCounter count(String testType) {
        return count(TestType.valueOf(testType.toUpperCase()));
    }

    public int getTotalTestCount() {
        return testOutcomes.getTotal();
    }

    /**
     * @return Formatted version of the test coverage metrics
     */
    public RequirementsPercentageFormatter getFormattedPercentage() {
        return new RequirementsPercentageFormatter(getProportion());
    }

    public RequirementsPercentageFormatter getFormattedPercentage(String testType) {
        return new RequirementsPercentageFormatter(proportionOf(testType));
    }

    public RequirementsPercentageFormatter getFormattedPercentage(TestType testType) {
        return new RequirementsPercentageFormatter(proportionOf(testType));
    }

    private int totalEstimatedAndImplementedTests() {
        int totalImplementedTests = getTotalTestCount();
        return totalImplementedTests + getEstimatedUnimplementedTests();
    }

    public int getEstimatedUnimplementedTests() {
        return getRequirementsWithoutTestsCount() * estimatedTestsPerRequirement();
    }

    private int estimatedTestsPerRequirement() {
        return ThucydidesSystemProperty.THUCYDIDES_ESTIMATED_TESTS_PER_REQUIREMENT.integerFrom(environmentVariables, DEFAULT_TESTS_PER_REQUIREMENT);
    }

    public RequirementsProportionCounter getProportion() {
        return proportionOf(TestType.ANY);
    }

    public RequirementsProportionCounter proportionOf(String testType) {
        return proportionOf(TestType.valueOf(testType.toUpperCase()));
    }

    public RequirementsProportionCounter proportionOf(TestType testType) {
        return new RequirementsProportionCounter(testType, testOutcomes, totalEstimatedAndImplementedTests());
    }

    public RequirementsOutcomes getReleasedRequirementsFor(Release release) {
        List<Requirement> matchingRequirements = Lists.newArrayList();
        List<TestOutcome> matchingTestOutcomes = Lists.newArrayList();

        // Add all test outcomes with a matching release

        List<RequirementOutcome> requirementOutcomes
                = releaseManager.enrichRequirementsOutcomesWithReleaseTags(getRequirementOutcomes());

        for (RequirementOutcome outcome : requirementOutcomes) {
            Collection<String> releaseVersions = outcome.getReleaseVersions();
            if (releaseVersions.contains(release.getName())) {
                List<TestOutcome> outcomesForRelease = outcomesForRelease(outcome.getTestOutcomes().getOutcomes(), release.getName());
                if (!outcomesForRelease.isEmpty()) {
                    matchingTestOutcomes.addAll(outcomesForRelease);
                    matchingRequirements.add(outcome.getRequirement());
                }
            }
        }
        matchingRequirements = removeRequirementsWithoutTestsFrom(matchingRequirements);
        return new RequirementsOutcomes(Lists.newArrayList(matchingRequirements),
                TestOutcomes.of(matchingTestOutcomes),
                issueTracking,
                environmentVariables,
                requirementsTagProviders,
                reportNameProvider).withoutUnrelatedRequirements();
    }

    private List<Requirement> removeRequirementsWithoutTestsFrom(List<Requirement> requirements) {
        List<Requirement> prunedRequirements = Lists.newArrayList();
        for (Requirement requirement : requirements) {
            if (testsExistFor(requirement)) {
                List<Requirement> prunedChildren = removeRequirementsWithoutTestsFrom(requirement.getChildren());
                prunedRequirements.add(requirement.withChildren(prunedChildren));
            }
        }
        return ImmutableList.copyOf(prunedRequirements);
    }

    private boolean testsExistFor(Requirement requirement) {
        return !getTestOutcomes().forRequirement(requirement).getOutcomes().isEmpty();
    }

    private List<TestOutcome> outcomesForRelease(List<? extends TestOutcome> outcomes,
                                                 String releaseName) {
        releaseManager.enrichOutcomesWithReleaseTags(outcomes);
        return (List<TestOutcome>) filter(having(on(TestOutcome.class).getVersions(), hasItem(releaseName)), outcomes);
    }

    public RequirementsOutcomes withoutUnrelatedRequirements() {
        if (isEmpty(ThucydidesSystemProperty.THUCYDIDES_EXCLUDE_UNRELATED_REQUIREMENTS_OF_TYPE.from(environmentVariables))) {
            return this;
        }
        return new RequirementsOutcomes(
                reportNameProvider,
                pruned(requirementOutcomes),
                testOutcomes,
                parentRequirement,
                environmentVariables,
                issueTracking,
                requirementsTagProviders,
                releaseManager);
    }

    private List<RequirementOutcome> pruned(List<RequirementOutcome> requirementOutcomes) {
        List<RequirementOutcome> prunedRequirementsOutcomes = new ArrayList<>();

        for (RequirementOutcome requirementOutcome : requirementOutcomes) {
            if (!shouldPrune(requirementOutcome)) {
                prunedRequirementsOutcomes.add(requirementOutcome.withoutUnrelatedRequirements());
            }
        }
        return prunedRequirementsOutcomes;
    }

    public boolean shouldPrune(RequirementOutcome requirementOutcome) {
        return ((requirementOutcome.getTestCount() == 0)
                 && ExcludedUnrelatedRequirementTypes.definedIn(environmentVariables)
                .excludeUntestedRequirementOfType(requirementOutcome.getRequirement().getType()));
    }
}
