package net.thucydides.core.reports;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.model.formatters.TestCoverageFormatter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.RequirementsTree;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static net.thucydides.core.model.TestResult.*;
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagName;
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagType;
import static org.hamcrest.Matchers.is;

//import static ch.lambdaj.Lambda.*;

/**
 * A set of test outcomes, which lets you perform query operations on the test outcomes.
 * In particular, you can filter a set of test outcomes by tag type and by tag values.
 * Since these operations also return TestOutcomes, you can then further drill down into the test
 * outcome sets.
 * The TestOutcomes object will usually return a list of TestOutcome objects. You can also inject
 * statistics and test run history by using the withHistory() method. This will return a list
 * of TestOutcomeWithHistory instances.
 */
public class TestOutcomes {

    private final List<? extends TestOutcome> outcomes;
    private final Optional<TestOutcomes> rootOutcomes;
    private final double estimatedAverageStepCount;
    private final EnvironmentVariables environmentVariables;
    private final RequirementsService requirementsService;

    /**
     * A label indicating where these tests come from (e.g. the tag, the result status, etc).
     */
    private final String label;
    private final TestTag testTag;

    /**
     * Reference to the test statistics service provider, used to inject test history if required.
     */
    private static final Integer DEFAULT_ESTIMATED_TOTAL_STEPS = 3;

    static int outcomeCount = 0;

    public <T> T sum(Object iterable, T argument) {
        return Lambda.sum(iterable, argument);
    }

    @Inject
    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestTag testTag,
                           TestOutcomes rootOutcomes,
                           EnvironmentVariables environmentVariables) {
        outcomeCount = outcomeCount + outcomes.size();
        this.outcomes = ImmutableList.copyOf(outcomes);
        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
        this.testTag = testTag;
        this.rootOutcomes = Optional.fromNullable(rootOutcomes);
        this.environmentVariables = environmentVariables;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    protected TestOutcomes(List<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestOutcomes rootOutcomes,
                           EnvironmentVariables environmentVariables) {
        outcomeCount = outcomeCount + outcomes.size();
        this.outcomes = outcomes;
        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
        this.testTag = null;
        this.rootOutcomes = Optional.fromNullable(rootOutcomes);
        this.environmentVariables = environmentVariables;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label) {
        this(outcomes, estimatedAverageStepCount, label, null, null, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected TestOutcomes(List<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestTag tag) {
        this(outcomes, estimatedAverageStepCount, label, tag, null, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount) {
        this(outcomes, estimatedAverageStepCount, "");
    }

    public TestOutcomes withLabel(String label) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, label);
    }

    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }
    public TestOutcomes havingResult(String result) {
        return havingResult(TestResult.valueOf(result.toUpperCase()));
    }

    List<TestOutcome> outcomesFilteredByResult(TestResult... results) {
        List<TestOutcome> filteredOutcomes = Lists.newArrayList();
        List<TestResult> eligableResults = Lists.newArrayList(results);
        for(TestOutcome outcome : outcomes) {
            if (eligableResults.contains(outcome.getResult())) {
                filteredOutcomes.add(outcome);
            }
        }
        return filteredOutcomes;
    }

    List<TestOutcome> outcomesFilteredByTag(TestTag tag) {
        List<TestOutcome> filteredOutcomes = Lists.newArrayList();
        for(TestOutcome outcome : outcomes) {
            if (outcome.getTags().contains(tag)) {
                filteredOutcomes.add(outcome);
            }
        }
        return filteredOutcomes;
    }

    public TestOutcomes havingResult(TestResult result) {

        return TestOutcomes.of(outcomesFilteredByResult(result))
                .withLabel(labelForTestsWithStatus(result.name()))
                .withRootOutcomes(getRootOutcomes());
    }

    public static TestOutcomes of(Collection<? extends TestOutcome> outcomes) {
        return new TestOutcomes(outcomes,
                ConfiguredEnvironment.getConfiguration().getEstimatedAverageStepCount());
    }

    private static List<TestOutcome> NO_OUTCOMES = ImmutableList.of();

    public static TestOutcomes withNoResults() {
        return new TestOutcomes(NO_OUTCOMES,
                                ConfiguredEnvironment.getConfiguration().getEstimatedAverageStepCount());
    }


    public String getLabel() {
        return label;
    }

    /**
     * @return The list of all of the different tag types that appear in the test outcomes.
     */
    public List<String> getTagTypes() {
        Set<String> tagTypes = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            addTagTypesFrom(outcome, tagTypes);
        }
        return sort(ImmutableList.copyOf(tagTypes), on(String.class));
    }

    public List<String> getFirstClassTagTypes() {
        Set<String> tagTypes = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            addTagTypesFrom(outcome, tagTypes);
        }
        tagTypes.remove("version");
        tagTypes.remove("feature");
        tagTypes.remove("story");
        tagTypes.removeAll(getRequirementTagTypes());
        return sort(ImmutableList.copyOf(tagTypes), on(String.class));
    }

    public List<String> getRequirementTagTypes() {
       List<String> tagTypes = Lists.newArrayList();

       List<String> candidateTagTypes = requirementsService.getRequirementTypes();
       for(String tagType : candidateTagTypes) {
           if (getTagTypes().contains(tagType)) {
               tagTypes.add(tagType);
           }
       }
       return ImmutableList.copyOf(tagTypes);
    }

    /**
     * @return The list of all the names of the different tags in these test outcomes
     */
    public List<String> getTagNames() {
        Set<String> tags = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            addTagNamesFrom(outcome, tags);
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }

    private void addTagNamesFrom(TestOutcome outcome, Set<String> tags) {
        for (TestTag tag : outcome.getTags()) {
            String normalizedForm = tag.getName().toLowerCase();
            if (!tags.contains(normalizedForm)) {
                tags.add(normalizedForm);
            }
        }
    }

    private void addTagTypesFrom(TestOutcome outcome, Set<String> tags) {
        for (TestTag tag : outcome.getTags()) {
            String normalizedForm = tag.getType().toLowerCase();
            if (!tags.contains(normalizedForm)) {
                tags.add(normalizedForm);
            }
        }
    }

    /**
     * @return The list of all the different tags in these test outcomes
     */
    public List<TestTag> getTags() {
        Set<TestTag> tags = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            tags.addAll(outcome.getTags());
        }
        return ImmutableList.copyOf(tags);
    }

    /**
     * @return The list of all the tags associated with a given tag type.
     */
    public List<TestTag> getTagsOfType(String tagType) {
        Set<TestTag> tags = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            tags.addAll(tagsOfType(tagType).in(outcome));
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }


    /**
     * @return The list of all the tags associated with a given tag type.
     */
    public List<TestTag> getMostSpecificTagsOfType(String tagType) {
        Set<TestTag> tags = Sets.newHashSet();
        for (TestOutcome outcome : outcomes) {
            List<TestTag> mostSpecificOutcomeTags = removeGeneralTagsFrom(tagsOfType(tagType).in(outcome));
            tags.addAll(mostSpecificOutcomeTags);
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }

    private List<TestTag> removeGeneralTagsFrom(List<TestTag> tags) {
        List<TestTag> specificTags = Lists.newArrayList();

        for(TestTag tag : tags) {
            if (!moreSpecificTagExists(tag, tags)) {
                specificTags.add(tag);
            }
        }
        return specificTags;
    }

    private boolean moreSpecificTagExists(TestTag generalTag, List<TestTag> tags) {
        for(TestTag tag : tags) {
            if (tag.getName().endsWith("/" + generalTag.getName())) {
                return true;
            }
        }
        return false;
    }


    public List<TestTag> getTagsOfTypeExcluding(String tagType, String excludedTag) {
        Set<TestTag> tags = Sets.newHashSet();

        for (TestOutcome outcome : outcomes) {
            List<TestTag> allTagsOfType = removeGeneralTagsFrom(tagsOfType(tagType).in(outcome));
            allTagsOfType = removeExcluded(allTagsOfType, excludedTag);
            tags.addAll(allTagsOfType);
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }

    private List<TestTag> removeExcluded(List<TestTag> allTagsOfType, String excludedTag) {
        List<TestTag> tags = Lists.newArrayList();
        for (TestTag tag: allTagsOfType) {
            if (!tag.getName().equalsIgnoreCase(excludedTag)) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private TagFinder tagsOfType(String tagType) {
        return new TagFinder(tagType);
    }

    public TestOutcomes getRootOutcomes() {
        return rootOutcomes.or(this);
    }

    public TestOutcomes forRequirement(Requirement requirement) {

        Set<TestOutcome> testOutcomesForThisRequirement = Sets.newHashSet();
        for(Requirement childRequirement : RequirementsTree.forRequirement(requirement).asFlattenedList()) {
            testOutcomesForThisRequirement.addAll(
                    withTag(childRequirement.asTag()).getOutcomes()
            );
            if (childRequirement.getCardNumber() != null) {
                testOutcomesForThisRequirement.addAll(
                        withCardNumber(childRequirement.getCardNumber()).getOutcomes()
                );
            }
        }

        return TestOutcomes.of(testOutcomesForThisRequirement)
                .withLabel(requirement.getDisplayName())
                .withTestTag(requirement.asTag())
                .withRootOutcomes(getRootOutcomes());
    }

    public boolean containsTag(TestTag testTag) {
        return getTags().contains(testTag);
    }

    public DateTime getStartTime() {
        return min(outcomes, on(TestOutcome.class).getStartTime());
    }

    public TestOutcomes ofType(TestType testType) {
        List<TestOutcome> filteredOutcomes = Lists.newArrayList();
        for(TestOutcome outcome : outcomes) {
            if (outcome.typeCompatibleWith(testType)) {
                filteredOutcomes.add(outcome);
            }
        }
        return TestOutcomes.of(filteredOutcomes);
    }

    public TestOutcomes withResult(TestResult result) {
        List<TestOutcome> filteredOutcomes = Lists.newArrayList();
        for(TestOutcome outcome : outcomes) {
            if (outcome.getResult() == result) {
                filteredOutcomes.add(outcome);
            }
        }
        return TestOutcomes.of(filteredOutcomes);
    }

    public TestOutcomes withRequirementsTags() {
        for (TestOutcome outcome : outcomes) {
            List<TestTag> outcomeTags = Lists.newArrayList(outcome.getTags());
            List<Requirement> parentRequirements = requirementsService.getAncestorRequirementsFor(outcome);
            for(Requirement requirement : parentRequirements) {
                outcomeTags.add(requirement.asTag());
            }
            outcome.addTags(outcomeTags);
        }
        return this;
    }

    private static class TagFinder {
        private final String tagType;

        private TagFinder(String tagType) {
            this.tagType = tagType.toLowerCase();
        }

        List<TestTag> in(TestOutcome testOutcome) {
            List<TestTag> matchingTags = Lists.newArrayList();
            for (TestTag tag : testOutcome.getTags()) {
                if (tag.normalisedType().equals(tagType)) {
                    matchingTags.add(tag);
                }
            }
            return matchingTags;
        }
    }

    /**
     * Find the test outcomes with a given tag type
     *
     * @param tagType the tag type we are filtering on
     * @return A new set of test outcomes for this tag type
     */
    public TestOutcomes withTagType(String tagType) {
        return TestOutcomes.of(filter(havingTagType(tagType), outcomes)).withLabel(tagType).withRootOutcomes(this.getRootOutcomes());
    }

    private TestOutcomes withRootOutcomes(TestOutcomes rootOutcomes) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, this.label, this.testTag, rootOutcomes, environmentVariables);
    }

    /**
     * Find the test outcomes with a given tag name
     *
     * @param tagName the name of the tag type we are filtering on
     * @return A new set of test outcomes for this tag name
     */
    public TestOutcomes withTag(String tagName) {
        return TestOutcomes.of(filter(havingTagName(tagName), outcomes)).withLabel(tagName).withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes withTag(TestTag tag) {
        List<? extends TestOutcome> outcomesWithMatchingTag = matchingOutcomes(outcomes, tag);
        return TestOutcomes.of(outcomesWithMatchingTag)
                           .withLabel(tag.getShortName())
                           .withTestTag(tag)
                           .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes withCardNumber(String issueCardNumber) {
        List<? extends TestOutcome> outcomesWithMatchingTag
                = matchingOutcomes(outcomes, TestTag.withName(issueCardNumber).andType("issue"));
        return TestOutcomes.of(outcomesWithMatchingTag)
                .withTestTag(TestTag.withName(issueCardNumber).andType("issue"))
                .withRootOutcomes(getRootOutcomes());
    }
    private TestOutcomes withTestTag(TestTag tag) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, label, tag);
    }

    public TestOutcomes withTags(List<TestTag> tags) {
        List<TestOutcome> filteredOutcomes = Lists.newArrayList();
        for (TestTag tag : tags) {
            filteredOutcomes.addAll(matchingOutcomes(outcomes, tag));
        }
        return TestOutcomes.of(filteredOutcomes);
    }

    private List<? extends TestOutcome> matchingOutcomes(List<? extends TestOutcome> outcomes, TestTag tag) {
        List<TestOutcome> matchingOutcomes = Lists.newArrayList();
        for (TestOutcome outcome : outcomes) {
            if (isAnIssue(tag) && (outcome.hasIssue(tag.getName()))) {
                matchingOutcomes.add(outcome);
            } else if (outcome.hasTag(tag)) {
                matchingOutcomes.add(outcome);
            } else if (outcome.hasAMoreGeneralFormOfTag(tag)) {
                matchingOutcomes.add(outcome);
            }
        }
        return matchingOutcomes;
    }


    private boolean isAnIssue(TestTag tag) {
        return tag.getType().equalsIgnoreCase("issue");
    }

    public TestOutcomes getFailingOrErrorTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ERROR, TestResult.FAILURE))
                .withLabel(labelForTestsWithStatus("unsuccessful tests"))
                .withRootOutcomes(getRootOutcomes());
    }

    /**
     * Find the failing test outcomes in this set
     *
     * @return A new set of test outcomes containing only the failing tests
     */
    public TestOutcomes getFailingTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.FAILURE))
                .withLabel(labelForTestsWithStatus("failing tests"))
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getErrorTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ERROR))
                .withLabel(labelForTestsWithStatus("tests with errors"))
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getManualTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ERROR))
                .withLabel(labelForTestsWithStatus("tests with errors"))
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getCompromisedTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.COMPROMISED))
                .withLabel(labelForTestsWithStatus("compromised tests"))
                .withRootOutcomes(getRootOutcomes());
    }

    private String labelForTestsWithStatus(String status) {
        if (StringUtils.isEmpty(label)) {
            return status;
        } else {
            return label + " (" + status + ")";
        }
    }

    /**
     * Find the successful test outcomes in this set
     *
     * @return A new set of test outcomes containing only the successful tests
     */
    public TestOutcomes getPassingTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.SUCCESS))
                .withLabel(labelForTestsWithStatus("passing tests"))
                .withRootOutcomes(getRootOutcomes());
    }

    /**
     * Find the pending or ignored test outcomes in this set
     *
     * @return A new set of test outcomes containing only the pending or ignored tests
     */
    public TestOutcomes getPendingTests() {

        List<TestOutcome> pendingOrSkippedOutcomes = outcomesWithResults(outcomes, PENDING, SKIPPED);
        return TestOutcomes.of(pendingOrSkippedOutcomes)
                .withLabel(labelForTestsWithStatus("pending tests"))
                .withRootOutcomes(getRootOutcomes());

    }

    private List<TestOutcome> outcomesWithResults(List<? extends TestOutcome> outcomes,
                                                  TestResult... possibleResults) {
        List<TestOutcome> validOutcomes = Lists.newArrayList();
        List<TestResult> possibleResultsList = Arrays.asList(possibleResults);
        for (TestOutcome outcome : outcomes) {
            if (possibleResultsList.contains(outcome.getResult())) {
                validOutcomes.add(outcome);
            }
        }
        return validOutcomes;
    }

    /**
     * @return The list of TestOutcomes contained in this test outcome set.
     */
    public List<? extends TestOutcome> getTests() {
        return sort(outcomes, on(TestOutcome.class).getTitle());
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public Long getDuration() {
        Long total = 0L;
        for (TestOutcome outcome : outcomes) {
            total += outcome.getDuration();
        }
        return total;
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public double getDurationInSeconds() {
        return TestDuration.of(getDuration()).inSeconds();
    }

    /**
     * @return The total number of test runs in this set (including rows in data-driven tests).
     */
    public int getTotal() {
        int total = 0;
        for(TestOutcome outcome : outcomes) {
            total += outcome.getTestCount();
        }
        return total;
//        return sum(outcomes, on(TestOutcome.class).getTestCount());
    }

    /**
     * The total number of test scenarios (a data-driven test is counted as one test scenario).
     */
    public int getTotalTestScenarios() {
        return outcomes.size();
    }

    public List<? extends TestOutcome> getOutcomes() {
        return outcomes; //ImmutableList.copyOf(outcomes);
    }

    /**
     * @return The overall result for the tests in this test outcome set.
     */
    public TestResult getResult() {
        return TestResultList.overallResultFrom(getCurrentTestResults());
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(outcomes, toTestResults());
    }

    private Converter<? extends TestOutcome, TestResult> toTestResults() {
        return new Converter<TestOutcome, TestResult>() {
            public TestResult convert(final TestOutcome step) {
                return step.getResult();
            }
        };
    }

    /**
     * @return The total number of nested steps in these test outcomes.
     */
    public int getStepCount() {
        int stepCount = 0;
        for (TestOutcome outcome : outcomes) {
            stepCount += outcome.getNestedStepCount();
        }
        return stepCount;
//        return sum(extract(outcomes, on(TestOutcome.class).getNestedStepCount())).intValue();
    }

    /**
     * @param testType 'manual' or 'automated' (this is a string because it is mainly called from the freemarker templates
     */
    public int successCount(String testType) {
        return sum(outcomes, on(TestOutcome.class).countResults(SUCCESS, TestType.valueOf(testType.toUpperCase())));
    }


    public OutcomeCounter getTotalTests() {
        return count(TestType.ANY);
    }

    public ScenarioOutcomeCounter getTotalScenarios() {
        return new ScenarioOutcomeCounter(TestType.ANY, this);
    }

    public OutcomeCounter count(String testType) {
        return count(TestType.valueOf(testType.toUpperCase()));
    }

    public OutcomeCounter count(TestType testType) {
        return new OutcomeCounter(testType, this);
    }

    public OutcomeProportionCounter getProportion() {
        return proportionOf(TestType.ANY);
    }

    public OutcomeProportionCounter proportionOf(String testType) {
        return proportionOf(TestType.valueOf(testType.toUpperCase()));
    }

    public OutcomeProportionCounter proportionOf(TestType testType) {
        return new OutcomeProportionCounter(testType);

    }

    public class OutcomeProportionCounter extends TestOutcomeCounter {

        public OutcomeProportionCounter(TestType testType) {
            super(testType);
        }

        public Double withResult(String expectedResult) {
            if (!TestResult.existsWithName(expectedResult.toUpperCase())) {
                return 0.0;
            }
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public Double withResult(TestResult testResult) {
            int matchingTestCount = countTestsWithResult(testResult, testType);
            return (getTotal() == 0) ? 0 :  (matchingTestCount / (double) getTotal());
        }

        public Double withIndeterminateResult() {
            int pendingCount = countTestsWithResult(TestResult.PENDING, testType);
            int ignoredCount =  countTestsWithResult(TestResult.IGNORED, testType);
            int skippedCount =  countTestsWithResult(TestResult.SKIPPED, testType);
            return (getTotal() == 0) ? 0 : ((pendingCount + skippedCount + ignoredCount) / (double) getTotal());
        }
        public Double withFailureOrError() {
            return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR) + withResult(TestResult.COMPROMISED);
        }
    }

    public OutcomeProportionStepCounter getPercentSteps() {
        return proportionalStepsOf(TestType.ANY);
    }

    public OutcomeProportionStepCounter proportionalStepsOf(String testType) {
        return proportionalStepsOf(TestType.valueOf(testType.toUpperCase()));
    }

    public OutcomeProportionStepCounter proportionalStepsOf(TestType testType) {
        return new OutcomeProportionStepCounter(testType);
    }

    public OutcomeProportionStepCounter decimalPercentageSteps(String testType) {
        return new OutcomeProportionStepCounter(TestType.valueOf(testType.toUpperCase()));
    }

    public class OutcomeProportionStepCounter extends TestOutcomeCounter  {

        public OutcomeProportionStepCounter(TestType testType) {
            super(testType);
        }

        public Double withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public Double withResult(TestResult expectedResult) {
            int matchingStepCount = countStepsWithResult(expectedResult, testType);
            return (matchingStepCount / (double) getEstimatedTotalStepCount());
        }

        public Double withIndeterminateResult() {
            int pendingCount = countStepsWithResult(TestResult.PENDING, testType);
            int ignoredCount =  countStepsWithResult(TestResult.IGNORED, testType);
            int skippedCount =  countStepsWithResult(TestResult.SKIPPED, testType);
            return ((pendingCount + skippedCount + ignoredCount) / (double) getEstimatedTotalStepCount());
        }
    }

    public TestCoverageFormatter.FormattedPercentageStepCoverage getFormattedPercentageSteps() {
        return new TestCoverageFormatter(this).getPercentSteps();
    }

    public TestCoverageFormatter.FormattedPercentageCoverage getFormattedPercentage() {
        return new TestCoverageFormatter(this).getPercentTests();
    }

    public TestCoverageFormatter.FormattedPercentageCoverage getFormattedPercentage(String testType) {
        this.getFormattedPercentage().withIndeterminateResult();
        return new TestCoverageFormatter(this).percentTests(testType);
    }

    public TestCoverageFormatter.FormattedPercentageCoverage getFormattedPercentage(TestType testType) {
        return new TestCoverageFormatter(this).percentTests(testType);
    }

    /**
     * @return Formatted version of the test coverage metrics
     */
    public TestCoverageFormatter getFormatted() {
        return new TestCoverageFormatter(this);
    }

    private int countStepsWithResult(TestResult expectedResult, TestType testType) {
//        int stepCount = sum(outcomes, on(TestOutcome.class).countNestedStepsWithResult(expectedResult, testType));
        int stepCount = 0;
        for(TestOutcome outcome : outcomes) {
            stepCount += outcome.countNestedStepsWithResult(expectedResult, testType);
        }

        if ((stepCount == 0) && aMatchingTestExists(expectedResult, testType)) {
            return (int) Math.round(getAverageTestSize());
        }
        return stepCount;
    }

    private boolean aMatchingTestExists(TestResult expectedResult, TestType testType) {
        return (countTestsWithResult(expectedResult, testType) > 0);
    }

    protected int countTestsWithResult(TestResult expectedResult, TestType testType) {
        int total = 0;
        for(TestOutcome outcome : outcomes) {
            total += outcome.countResults(expectedResult, testType);
        }
        return total;
//        return sum(outcomes, on(TestOutcome.class).countResults(expectedResult, testType));
    }

    private Integer getEstimatedTotalStepCount() {
        int estimatedTotalSteps = (getStepCount() + estimatedUnimplementedStepCount());
        return (estimatedTotalSteps == 0) ? DEFAULT_ESTIMATED_TOTAL_STEPS : estimatedTotalSteps;
    }

    private Integer estimatedUnimplementedStepCount() {
        return (int) (Math.round(getAverageTestSize() * totalUnimplementedTests()));
    }

    public double getAverageTestSize() {
        if (totalImplementedTests() > 0) {
            return ((double) getStepCount()) / totalImplementedTests();
        } else {
            return estimatedAverageStepCount;
        }
    }

//    public double getRecentStability() {
//        if (outcomes.isEmpty()) {
//            return 0.0;
//        } else {
//            return sum(outcomes, on(TestOutcome.class).getRecentStability()) / getTestCount();
//        }
//    }

//    public double getOverallStability() {
//        if (outcomes.isEmpty()) {
//            return 0.0;
//        } else {
//            return sum(outcomes, on(TestOutcome.class).getOverallStability()) / getTestCount();
//        }
//    }

    private int totalUnimplementedTests() {
        return getTotal() - totalImplementedTests();
    }

    public int getTestCount() {
        int total = 0;
        for(TestOutcome outcome : outcomes) {
            total += outcome.getTestCount();
        }
        return total;
//        return sum(outcomes, on(TestOutcome.class).getTestCount());
    }

    private int totalImplementedTests() {
        int total = 0;
        for(TestOutcome outcome : outcomes) {
            total += outcome.getImplementedTestCount();
        }
        return total;
//        return sum(outcomes, on(TestOutcome.class).getImplementedTestCount());
    }

    public boolean hasDataDrivenTests() {
        for(TestOutcome outcome : outcomes) {
            if (outcome.isDataDriven()) {
                return true;
            }
        }
        return false;
 //       return !filter(having(on(TestOutcome.class).isDataDriven(), is(true)), outcomes).isEmpty();
    }

    public int getTotalDataRows() {
        int total = 0;
        for(TestOutcome outcome : outcomes) {
            total += (outcome.getDataTable() != null) ? outcome.getDataTable().getSize() : 0;
        }
        return total;

//        List<? extends TestOutcome> datadrivenTestOutcomes = filter(having(on(TestOutcome.class).isDataDriven(), is(true)), outcomes);
//        return sum(datadrivenTestOutcomes, on(TestOutcome.class).getDataTable().getSize());
    }

    public TestOutcomeMatcher findMatchingTags() {
        return new TestOutcomeMatcher(this);
    }

    public static final class TestOutcomeMatcher {

        private final TestOutcomes outcomes;
        private Optional<List<Matcher<String>>> nameMatcher = Optional.absent();
        private Optional<Matcher<String>> typeMatcher = Optional.absent();

        public TestOutcomeMatcher(TestOutcomes outcomes) {
            this.outcomes = outcomes;
        }

        @SuppressWarnings("unchecked")
        public TestOutcomeMatcher withName(Matcher<String> nameMatcher) {
            List<Matcher<String>> matchers = Lists.newArrayList(nameMatcher);
            this.nameMatcher = Optional.of(matchers);
            return this;
        }

        public TestOutcomeMatcher withNameIn(List<Matcher<String>>  nameMatchers) {
            List<Matcher<String>> matchers = Lists.newArrayList(nameMatchers);
            this.nameMatcher = Optional.of(matchers);
            return this;
        }

        public TestOutcomeMatcher withName(String name) {
            return withName(is(name));
        }

        public TestOutcomeMatcher withType(Matcher<String> typeMatcher) {
            this.typeMatcher = Optional.of(typeMatcher);
            return this;
        }

        public TestOutcomeMatcher withType(String type) {
            return withType(is(type));
        }

        public List<TestTag> list() {
            List<TestTag> matches = Lists.newArrayList();
            for(TestTag tag : outcomes.getTags()) {
                if (compatibleTag(tag)) {
                    matches.add(tag);
                }
            }
            Collections.sort(matches);
            return matches;
        }

        private boolean compatibleTag(TestTag tag) {
            if (nameMatcher.isPresent()) {
                if (!matches(tag.getName(), nameMatcher.get())) {
                    return false;
                }
            }
            if (typeMatcher.isPresent()) {
                if (!typeMatcher.get().matches(tag.getType())) {
                    return false;
                }
            }
            return true;
        }

        private boolean matches(String name, List<Matcher<String>> matchers) {
            for(Matcher<String> match : matchers) {
                if (match.matches(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}
