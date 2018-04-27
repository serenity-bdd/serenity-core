package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.collect.NewSet;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.model.flags.Flag;
import net.thucydides.core.model.flags.FlagCounts;
import net.thucydides.core.model.formatters.TestCoverageFormatter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.RequirementsTree;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static org.hamcrest.Matchers.is;

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

    @Inject
    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestTag testTag,
                           TestOutcomes rootOutcomes,
                           EnvironmentVariables environmentVariables) {
        outcomeCount = outcomeCount + outcomes.size();

        this.outcomes = Collections.unmodifiableList(sorted(outcomes));

        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
        this.testTag = testTag;
        this.rootOutcomes = Optional.ofNullable(rootOutcomes);
        this.environmentVariables = environmentVariables;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestOutcomes rootOutcomes,
                           EnvironmentVariables environmentVariables) {
        outcomeCount = outcomeCount + outcomes.size();
        this.outcomes = Collections.unmodifiableList(sorted(outcomes));
        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
        this.testTag = null;
        this.rootOutcomes = Optional.ofNullable(rootOutcomes);
        this.environmentVariables = environmentVariables;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    private List<TestOutcome> sorted(Collection<? extends TestOutcome> outcomes) {
        return outcomes.stream()
                .sorted(Comparator.comparing(TestOutcome::getPath,
                        Comparator.nullsFirst(naturalOrder()))
                .thenComparing(Comparator.comparing(TestOutcome::getStartTime,
                               Comparator.nullsFirst(naturalOrder()))))
                .collect(Collectors.toList());
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

        List<TestResult> eligableResults = NewList.of(results);

        return outcomes.stream()
                .filter(outcome -> eligableResults.contains(outcome.getResult()))
                .collect(Collectors.toList());

    }

    List<TestOutcome> outcomesFilteredByTag(TestTag tag) {
        return outcomes.stream()
                .filter(outcome -> outcome.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    public TestOutcomes havingResult(TestResult result) {

        return TestOutcomes.of(outcomesFilteredByResult(result))
                .withLabel(labelForTestsWithStatus(result.name()))
                .withRootOutcomes(getRootOutcomes());
    }

    public static TestOutcomes of(Collection<? extends TestOutcome> outcomes) {
        return new TestOutcomes(outcomes,ConfiguredEnvironment.getConfiguration().getEstimatedAverageStepCount());
    }

    private static List<TestOutcome> NO_OUTCOMES = new ArrayList();


    public static TestOutcomes withNoResults() {
        return new TestOutcomes(NO_OUTCOMES,ConfiguredEnvironment.getConfiguration().getEstimatedAverageStepCount());

    }

    private Map<? extends Flag, Integer> flagCount = null;

    public Map<? extends Flag, Integer> getFlagCounts() {

        if (flagCount != null) { return flagCount; }

        flagCount = FlagCounts.in(getOutcomes()).asAMap();

        return flagCount;
    }

    public boolean haveFlags() {
        return !getFlags().isEmpty();
    }

    public Set<? extends Flag> getFlags() {
        return getFlagCounts().keySet();
    }

    public Integer flagCountFor(Flag flag) {
        return getFlagCounts().get(flag);
    }



    public String getLabel() {
        return label;
    }

    /**
     * @return The list of all of the different tag types that appear in the test outcomes.
     */
    public List<String> getTagTypes() {
        return outcomes.stream()
                .flatMap(this::tagTypesIn)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private final Set<String> SECOND_CLASS_TAG_TYPES = NewSet.of("version","feature", "story");

    public List<String> getFirstClassTagTypes() {
        return getTagTypes()
                    .stream()
                    .filter(tagType -> !SECOND_CLASS_TAG_TYPES.contains(tagType))
                    .filter(tagType -> !getRequirementTagTypes().contains(tagType))
                    .collect(Collectors.toList());
    }

    public List<String> getRequirementTagTypes() {
       return requirementsService.getRequirementTypes()
               .stream()
               .filter(tagType -> getTagTypes().contains(tagType))
               .collect(Collectors.toList());
    }

    /**
     * @return The list of all the names of the different tags in these test outcomes
     */
    public List<String> getTagNames() {
        return outcomes.stream()
                .flatMap(this::tagNamesIn)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private Stream<String> tagTypesIn(TestOutcome outcome) {
        return outcome.getTags()
                .stream()
                .map(tag -> tag.getType().toLowerCase());
    }

    private Stream<String> tagNamesIn(TestOutcome outcome) {
        return outcome.getTags()
                .stream()
                .map(tag -> tag.getName().toLowerCase());
    }

    /**
     * @return The list of all the different tags in these test outcomes
     */
    public List<TestTag> getTags() {
        return outcomes.stream()
                .flatMap(outcome -> outcome.getTags().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * @return The list of all the tags associated with a given tag type.
     */
    public List<TestTag> getTagsOfType(String tagType) {
        return outcomes.stream()
                .flatMap(outcome -> tagsOfType(tagType).from(outcome))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * @return The list of all the tags associated with a given tag type.
     */
    public List<TestTag> getMostSpecificTagsOfType(String tagType) {
        return outcomes.stream()
                .flatMap(outcome -> removeGeneralTagsFrom(tagsOfType(tagType).in(outcome)))
                .sorted()
                .collect(Collectors.toList());
    }

    private Stream<TestTag> removeGeneralTagsFrom(List<TestTag> tags) {
        return tags.stream().filter(
                tag -> !moreSpecificTagExists(tag, tags)
        );
    }

    private boolean moreSpecificTagExists(TestTag generalTag, List<TestTag> tags) {
        return tags.stream()
                .anyMatch(tag -> tag.getName().endsWith("/" + generalTag.getName()));
    }


    public List<TestTag> getTagsOfTypeExcluding(String tagType, String excludedTag) {

        Predicate<TestTag> withExcludedTags = tag -> !tag.getName().equalsIgnoreCase(excludedTag);

        List<TestTag> allTags = outcomes.stream()
                .flatMap(outcome -> tagsOfType(tagType).from(outcome))
                .collect(Collectors.toList());

        return allTags.stream()
                .filter( tag -> !moreSpecificTagExists(tag, allTags) )
                .filter( withExcludedTags )
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<TestTag> removeExcluded(List<TestTag> allTagsOfType, String excludedTag) {
        Predicate<TestTag> withExcludedTags = tag -> !tag.getName().equalsIgnoreCase(excludedTag);

        return allTagsOfType.stream()
                .filter(withExcludedTags)
                .collect(Collectors.toList());
    }

    private TagFinder tagsOfType(String tagType) {
        return new TagFinder(tagType);
    }

    public TestOutcomes getRootOutcomes() {
        return rootOutcomes.orElse(this);
    }

    public TestOutcomes forRequirement(Requirement requirement) {

        Set<TestOutcome> testOutcomesForThisRequirement = new HashSet();
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

    public TestTag getTestTag() { return testTag; }

    public boolean containsTag(TestTag testTag) {
        return getTags().contains(testTag);
    }

    public Optional<ZonedDateTime> getStartTime() {
        return outcomes.stream()
                .filter(outcome -> outcome.getStartTime() != null)
                .map(TestOutcome::getStartTime)
                .sorted()
                .findFirst();
    }

    public TestOutcomes ofType(TestType testType) {
        List<TestOutcome> filteredOutcomes = outcomes
                .stream()
                .filter(outcome -> outcome.typeCompatibleWith(testType))
                .collect(Collectors.toList());

        return TestOutcomes.of(filteredOutcomes);
    }

    public TestOutcomes withResult(TestResult result) {

        List<TestOutcome> filteredOutcomes = outcomes
                .stream()
                .filter(outcome -> outcome.getResult() == result)
                .collect(Collectors.toList());

        return TestOutcomes.of(filteredOutcomes);
    }

    public TestOutcomes withRequirementsTags() {
        for (TestOutcome outcome : outcomes) {
            List<TestTag> outcomeTags = new ArrayList<>(outcome.getTags());
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

        Stream<TestTag> from(TestOutcome outcome) {
            return outcome.getTags().stream()
                    .filter(tag -> tag.normalisedType().equals(tagType));
        }

        List<TestTag> in(TestOutcome testOutcome) {
            return testOutcome.getTags().stream()
                    .filter(tag -> tag.normalisedType().equals(tagType))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Find the test outcomes with a given tag type
     *
     * @param tagType the tag type we are filtering on
     * @return A new set of test outcomes for this tag type
     */
    public TestOutcomes withTagType(String tagType) {

        List<TestOutcome> testOutcomesWithTags = outcomes.stream()
                .filter(outcome -> outcome.hasTagWithType(tagType))
                .collect(Collectors.toList());

        return TestOutcomes.of(testOutcomesWithTags).withLabel(tagType).withRootOutcomes(getRootOutcomes());
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

        List<TestOutcome> testOutcomesWithTags = outcomes.stream()
                .filter(outcome -> outcome.hasTagWithName(tagName))
                .collect(Collectors.toList());

        return TestOutcomes.of(testOutcomesWithTags).withLabel(tagName).withRootOutcomes(getRootOutcomes());
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
        Set<TestOutcome> filteredOutcomes = new HashSet<>();
        for (TestTag tag : tags) {
            filteredOutcomes.addAll(matchingOutcomes(outcomes, tag));
        }
        return TestOutcomes.of(filteredOutcomes);
    }

    private List<? extends TestOutcome> matchingOutcomes(List<? extends TestOutcome> outcomes, TestTag tag) {

        return outcomes.stream().filter(

                outcome -> (isAnIssue(tag) && (outcome.hasIssue(tag.getName())))
                            || (outcome.hasTag(tag))
                            || (outcome.hasAMoreGeneralFormOfTag(tag))

        ).collect(Collectors.toList());
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

        List<TestOutcome> pendingOrSkippedOutcomes = outcomesWithResults(outcomes, TestResult.PENDING, TestResult.SKIPPED);
        return TestOutcomes.of(pendingOrSkippedOutcomes)
                .withLabel(labelForTestsWithStatus("pending tests"))
                .withRootOutcomes(getRootOutcomes());

    }

    private List<TestOutcome> outcomesWithResults(List<? extends TestOutcome> outcomes,
                                                  TestResult... possibleResults) {
        List<TestOutcome> validOutcomes = new ArrayList<>();
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
        return outcomes;
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public Long getDuration() {
        return outcomes.stream()
                .mapToLong(TestOutcome::getDuration)
                .sum();
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
        return outcomes.stream()
                .mapToInt(TestOutcome::getTestCount)
                .sum();
    }

    /**
     * The total number of test scenarios (a data-driven test is counted as one test scenario).
     */
    public int getTotalTestScenarios() {
        return outcomes.size();
    }

    public List<? extends TestOutcome> getOutcomes() {
        return outcomes; //NewList.copyOf(outcomes);
    }

    /**
     * @return The overall result for the tests in this test outcome set.
     */
    public TestResult getResult() {
        return TestResultList.overallResultFrom(getCurrentTestResults());
    }

    private List<TestResult> getCurrentTestResults() {
        return outcomes.stream()
                .map(TestOutcome::getResult)
                .collect(Collectors.toList());
    }

    /**
     * @return The total number of nested steps in these test outcomes.
     */
    public int getStepCount() {
        return outcomes.stream()
                .mapToInt(TestOutcome::getNestedStepCount)
                .sum();
    }

    /**
     * @param testType 'manual' or 'automated' (this is a string because it is mainly called from the freemarker templates
     */
    public int successCount(String testType) {

        TestType expectedTestType = TestType.valueOf(testType.toUpperCase());

        return outcomes.stream()
                        .mapToInt(outcome -> outcome.countResults(TestResult.SUCCESS, expectedTestType))
                        .sum();
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

        int stepCount =  outcomes.stream()
                .mapToInt(outcome -> outcome.countNestedStepsWithResult(expectedResult, testType))
                .sum();

        if ((stepCount == 0) && aMatchingTestExists(expectedResult, testType)) {
            return (int) Math.round(getAverageTestSize());
        }
        return stepCount;
    }

    private boolean aMatchingTestExists(TestResult expectedResult, TestType testType) {
        return (countTestsWithResult(expectedResult, testType) > 0);
    }

    protected int countTestsWithResult(TestResult expectedResult, TestType testType) {
        return outcomes.stream()
                .mapToInt(outcome -> outcome.countResults(expectedResult, testType))
                .sum();
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

    private int totalUnimplementedTests() {
        return getTotal() - totalImplementedTests();
    }

    public int getTestCount() {
        return outcomes.stream()
                .mapToInt(TestOutcome::getTestCount)
                .sum();
    }

    private int totalImplementedTests() {
        return outcomes.stream()
                .mapToInt(TestOutcome::getImplementedTestCount)
                .sum();
    }

    public boolean hasDataDrivenTests() {
        return outcomes.stream().anyMatch(TestOutcome::isDataDriven);
    }

    public int getTotalDataRows() {
        return outcomes.stream()
                .mapToInt(TestOutcome::getDataTableRowCount)
                .sum();
    }

    public TestOutcomeMatcher findMatchingTags() {
        return new TestOutcomeMatcher(this);
    }

    public static final class TestOutcomeMatcher {

        private final TestOutcomes outcomes;
        private List<Matcher<String>> nameMatcher = null;
        private Matcher<String> typeMatcher = null;

        public TestOutcomeMatcher(TestOutcomes outcomes) {
            this.outcomes = outcomes;
        }

        @SuppressWarnings("unchecked")
        public TestOutcomeMatcher withName(Matcher<String> nameMatcher) {
            this.nameMatcher = NewList.of(nameMatcher);
            return this;
        }

        public TestOutcomeMatcher withNameIn(List<Matcher<String>>  nameMatchers) {
            this.nameMatcher = new ArrayList<>(nameMatchers);
            return this;
        }

        public TestOutcomeMatcher withName(String name) {
            return withName(is(name));
        }

        public TestOutcomeMatcher withType(Matcher<String> typeMatcher) {
            this.typeMatcher = typeMatcher;
            return this;
        }

        public TestOutcomeMatcher withType(String type) {
            return withType(is(type));
        }

        public List<TestTag> list() {
            return outcomes.getTags().stream()
                    .filter(this::compatibleTag)
                    .sorted()
                    .collect(Collectors.toList());
        }

        private boolean compatibleTag(TestTag tag) {

            if (nameMatcher != null) {
                if (!matches(tag.getName(), nameMatcher)) {
                    return false;
                }
            }
            if (typeMatcher != null) {
                if (!typeMatcher.matches(tag.getType())) {
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
