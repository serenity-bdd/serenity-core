package net.thucydides.core.reports;

import com.google.inject.Inject;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.collect.NewSet;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.model.flags.Flag;
import net.thucydides.core.model.flags.FlagCounts;
import net.thucydides.core.model.formatters.TestCoverageFormatter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.RequirementsTree;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.steps.TestSourceType;
import net.thucydides.core.tags.OutcomeTagFilter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
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
    private final TestResult resultFilter;
    private ZonedDateTime startTime;

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
                           TestResult resultFilter,
                           TestOutcomes rootOutcomes,
                           EnvironmentVariables environmentVariables) {
        outcomeCount = outcomeCount + outcomes.size();

        this.outcomes = sorted(outcomes);
        this.startTime = startTime().orElse(null);

        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
        this.testTag = testTag;
        this.resultFilter = resultFilter;
        this.rootOutcomes = Optional.ofNullable(rootOutcomes);
        this.environmentVariables = environmentVariables;
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    private List<TestOutcome> sorted(Collection<? extends TestOutcome> outcomes) {
        return outcomes.stream()
                .sorted(Comparator.comparing(TestOutcome::getPath,
                        Comparator.nullsFirst(naturalOrder()))
                .thenComparing(TestOutcome::getOrder, Comparator.nullsFirst(naturalOrder()))
                .thenComparing(TestOutcome::getStartTime, Comparator.nullsFirst(naturalOrder())))
                .collect(Collectors.toList());
    }

    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label) {
        this(outcomes, estimatedAverageStepCount, label, null, null, null, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected TestOutcomes(List<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestTag tag) {
        this(outcomes, estimatedAverageStepCount, label, tag, null, null, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected TestOutcomes(List<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount,
                           String label,
                           TestResult resultFilter) {
        this(outcomes, estimatedAverageStepCount, label, null, resultFilter, null, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected TestOutcomes(Collection<? extends TestOutcome> outcomes,
                           double estimatedAverageStepCount) {
        this(outcomes, estimatedAverageStepCount, "");
    }

    public TestOutcomes withLabel(String label) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, label);
    }

    public TestOutcomes withResultFilter(TestResult testResult) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, label, testResult);
    }

    public TestOutcomes filteredByEnvironmentTags() {

        OutcomeTagFilter outcomeFilter = new OutcomeTagFilter(environmentVariables);
        List<? extends TestOutcome> filteredOutcomes = outcomeFilter.outcomesFilteredByTagIn(getOutcomes());

        return TestOutcomes.of(filteredOutcomes).withLabel(label);
    }


    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }
    public TestOutcomes havingResult(String result) {
        return havingResult(TestResult.valueOf(result.toUpperCase()));
    }

    private List<TestOutcome> outcomesFilteredByResult(TestResult... results) {
        if (onlyPassing(results)) {
            return outcomesExclusivelyWithResults(results);
        } else {
            return outcomesWithAtLeastOneResultOf(results);
        }
    }

    private boolean onlyPassing(TestResult[] results) {
        return stream(results).allMatch( result -> result == TestResult.SUCCESS);
    }

    private List<TestOutcome> outcomesExclusivelyWithResults(TestResult... results) {
        List<TestResult> eligableResults = NewList.of(results);

        return outcomes.stream()
                .filter(outcome -> eligableResults.contains(outcome.getResult()))
                .collect(Collectors.toList());
    }

    private List<TestOutcome> outcomesWithAtLeastOneResultOf(TestResult... results) {
        return outcomes.stream()
                .filter(outcome -> outcomeHasResultFrom(outcome, results))
                .collect(Collectors.toList());
    }

    private boolean outcomeHasResultFrom(TestOutcome outcome, TestResult... results) {

        List<TestResult> eligableResults = NewList.of(results);

        if (!outcome.isDataDriven()) {
            return eligableResults.contains(outcome.getResult());
        }
        return outcome.getDataTable().getRows().stream().anyMatch(
                row -> eligableResults.contains(row.getResult())
        );
    }

    public TestOutcomes havingResult(TestResult result) {

        return TestOutcomes.of(outcomesFilteredByResult(result))
                .withLabel(labelForTestsWithStatus(result.name()))
                .withResultFilter(result)
                .withRootOutcomes(getRootOutcomes());
    }

    public static TestOutcomes of(Collection<? extends TestOutcome> outcomes) {
        return new TestOutcomes(outcomes, SystemPropertiesConfiguration.DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
    }

    private static List<TestOutcome> NO_OUTCOMES = new ArrayList<>();


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

    private final static Set<String> SECOND_CLASS_TAG_TYPES = NewSet.of("version","feature", "story");

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
                .flatMap(outcome -> outcome.getAllTags().stream())
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

    private TagFinder tagsOfType(String tagType) {
        return new TagFinder(tagType);
    }

    public TestOutcomes getRootOutcomes() {
        return rootOutcomes.orElse(this);
    }


    private List<? extends TestOutcome> outcomesWithMatchingTagFor(Requirement childRequirement) {
        return withTag(childRequirement.asTag()).getOutcomes();
    }

    private List<? extends TestOutcome> outcomesWithMatchingCardNumberFor(Requirement childRequirement) {
        if (childRequirement.getCardNumber() != null) {
            return withCardNumber(childRequirement.getCardNumber()).getOutcomes();
        } else {
            return Collections.emptyList();
        }
    }


    private Stream<? extends TestOutcome> outcomesMatching(Requirement requirement) {
        return Stream.concat(
                outcomesWithMatchingTagFor(requirement).stream(),
                outcomesWithMatchingCardNumberFor(requirement).stream()
        );
    }

    public TestOutcomes forRequirement(Requirement requirement) {

        Set<TestOutcome> testOutcomesForThisRequirement = new HashSet<>();

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

    public boolean containsMatchingTag(TestTag containedTag) {
        return getTags().stream().anyMatch(
                tag -> tag.isAsOrMoreSpecificThan(containedTag) || containedTag.isAsOrMoreSpecificThan(tag)
        );
    }

    private Optional<ZonedDateTime> startTime() {
        return outcomes.stream()
                .map(TestOutcome::getStartTime)
                .filter(Objects::nonNull)
                .sorted()
                .findFirst();
    }

    public Optional<ZonedDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public TestOutcomes ofType(TestType testType) {
        List<TestOutcome> filteredOutcomes = outcomes
                .stream()
                .filter(outcome -> outcome.typeCompatibleWith(testType))
                .collect(Collectors.toList());

        return TestOutcomes.of(filteredOutcomes);
    }

    private boolean failedWith(TestOutcome outcome, String testFailureErrorType) {
        if (!outcome.isDataDriven()) {
            return (outcome.getTestFailureErrorType().equals(testFailureErrorType) && (outcome.getResult().isAtLeast(TestResult.FAILURE)));
        }
        return outcome.getTestSteps().stream()
                .anyMatch(
                        step -> step.getResult().isAtLeast(TestResult.FAILURE)
                                && step.getException() != null
                                && step.getException().getErrorType().equals(testFailureErrorType)
                );
    }

    public Integer scenarioCountWithResult(TestResult result) {
        return outcomes
                .stream()
                .mapToInt(outcome -> countScenariosWithResult(result, outcome))
                .sum();
    }

    private int countScenariosWithResult(TestResult result, TestOutcome outcome) {
        if (result == TestResult.UNSUCCESSFUL) {
            return countScenariosWithResults(outcome, TestResult.FAILURE, TestResult.ERROR, TestResult.COMPROMISED);
        } else {
            return countScenariosWithResults(outcome, result);
        }
    }

    private boolean hasResult(TestResult[] results, TestResult expectedResult) {
        for(int i = 0; i < results.length; i++) {
            if (results[i] == expectedResult) return true;
        }
        return false;
    }

    private int countScenariosWithResults(TestOutcome outcome, TestResult... results) {

        if (!outcome.isDataDriven()) {
            return (hasResult(results, outcome.getResult())) ? 1 : 0;
        }

        if (outcome.isManual()) {
            return (int) stepsWithResultIn(outcome.getTestSteps(), results);
        }

        if ((dataTableRowResultsAreUndefinedIn(outcome.getDataTable()) || isJUnit(outcome) || isJUnit5(outcome))
            && outcome.getTestSteps().size() >= outcome.getDataTable().getSize()) {
            return (int) stepsWithResultIn(outcome.getTestSteps(), results);
        }

        return (int) outcome.getDataTable().getRows().stream()
                .filter(row -> hasResult(results, row.getResult()))
                .count();
    }

    private boolean isJUnit(TestOutcome outcome) {
        return (outcome.getTestSource() == null) || (TestSourceType.TEST_SOURCE_JUNIT.getValue().equalsIgnoreCase(outcome.getTestSource()));
    }

    private boolean isJUnit5(TestOutcome outcome) {
        return (outcome.getTestSource() == null) || (TestSourceType.TEST_SOURCE_JUNIT5.getValue().equalsIgnoreCase(outcome.getTestSource()));
    }

    private long stepsWithResultIn(List<TestStep> steps, TestResult... results) {
        return steps.stream()
                .filter(step -> hasResult(results,step.getResult()))
                .count();
    }

    private boolean dataTableRowResultsAreUndefinedIn(DataTable dataTable) {
        return dataTable.getRows().stream().allMatch(row -> row.getResult() == TestResult.UNDEFINED);
    }

    public TestOutcomes withErrorType(String testFailureErrorType) {
        List<TestOutcome> filteredOutcomes = outcomes
                .stream()
                .filter(outcome -> failedWith(outcome, testFailureErrorType))
                .collect(Collectors.toList());

        return TestOutcomes.of(filteredOutcomes).withLabel("");
    }

    public TestOutcomes withResult(TestResult result) {

        List<TestOutcome> filteredOutcomes = outcomes
                .stream()
                .filter(outcome -> countScenariosWithResult(result, outcome) > 0)
                .collect(Collectors.toList());

        return TestOutcomes.of(filteredOutcomes);
    }

    public TestOutcomes withRequirementsTags() {
        for (TestOutcome outcome : outcomes) {
            List<TestTag> outcomeTags = new ArrayList<>(outcome.getAllTags());
            List<Requirement> parentRequirements = requirementsService.getAncestorRequirementsFor(outcome);
            for(Requirement requirement : parentRequirements) {
                outcomeTags.add(requirement.asTag());
            }
            outcome.addTags(outcomeTags);
        }
        return this;
    }

    public Optional<? extends TestOutcome> testOutcomeWithName(String name) {
        return outcomes.stream().filter(
                outcome -> outcome.getName().equalsIgnoreCase(name)
        ).findFirst();
    }

    public List<TestOutcome> testOutcomesWithName(String name) {
        return outcomes.stream().filter(
                outcome -> outcome.getName().equalsIgnoreCase(name)
        ).collect(Collectors.toList());
    }

    public long getFastestTestDuration() {
        return outcomes.stream()
                .filter(outcome -> outcome.getDuration() > 0)
                .mapToLong(this::minDurationOf)
                .min()
                .orElse(0);
    }

    public long getSlowestTestDuration() {
        return outcomes.stream()
                .filter(outcome -> outcome.getDuration() > 0)
                .mapToLong(this::maxDurationOf)
                .max()
                .orElse(0);
    }

    private Long maxDurationOf(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getTestSteps().stream().mapToLong(TestStep::getDuration).max().orElse(0);
        } else {
            return outcome.getDuration();
        }
    }

    private Long minDurationOf(TestOutcome outcome) {
        if (outcome.isDataDriven()) {
            return outcome.getTestSteps().stream().mapToLong(TestStep::getDuration).min().orElse(0);
        } else {
            return outcome.getDuration();
        }
    }

    public boolean containTestFor(Requirement requirement) {
        return requirement.getTags().stream().anyMatch(
                this::containsMatchingTag
        );
    }

    private static class TagFinder {
        private final String tagType;

        private TagFinder(String tagType) {
            this.tagType = tagType.toLowerCase();
        }

        Stream<TestTag> from(TestOutcome outcome) {
            return outcome.getAllTags().stream()
                    .filter(tag -> tag.normalisedType().equalsIgnoreCase(tagType));
        }

        List<TestTag> in(TestOutcome testOutcome) {
            return testOutcome.getAllTags().stream()
                    .filter(tag -> tag.normalisedType().equalsIgnoreCase(tagType))
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

    public TestOutcomes withTagTypes(List<String> tagTypes) {

        List<TestOutcome> testOutcomesWithTags = outcomes.stream()
                .filter(outcome -> outcome.hasTagWithTypes(tagTypes))
                .collect(Collectors.toList());

        return TestOutcomes.of(testOutcomesWithTags).withLabel(Joiner.on(",").join(tagTypes))
                           .withRootOutcomes(getRootOutcomes());
    }

    private TestOutcomes withRootOutcomes(TestOutcomes rootOutcomes) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, this.label, this.testTag, this.resultFilter, rootOutcomes, environmentVariables);
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

    public TestOutcomes withTags(Collection<TestTag> tags) {
        Set<TestOutcome> filteredOutcomes = new HashSet<>();
        for (TestTag tag : tags) {
            filteredOutcomes.addAll(matchingOutcomes(outcomes, tag));
        }
        return TestOutcomes.of(filteredOutcomes);
    }

    private List<? extends TestOutcome> matchingOutcomes(List<? extends TestOutcome> outcomes, TestTag tag) {

        return outcomes.stream()
                .filter(outcome -> hasMatchingTag(outcome, tag))
                .map(outcome -> outcome.withDataRowsfilteredbyTag(tag))
                .collect(Collectors.toList());
    }

    private List<? extends TestOutcome> matchingOutcomesWithTagsFrom(List<? extends TestOutcome> outcomes, Collection<TestTag> tags) {

        return outcomes.stream()
                .filter(outcome -> hasMatchingTagsFrom(outcome, tags))
                .map(outcome -> outcome.withDataRowsfilteredbyTagsFrom(tags))
                .collect(Collectors.toList());
    }

    private boolean hasMatchingTagsFrom(TestOutcome outcome, Collection<TestTag> tags) {
        return tags.stream().anyMatch( tag -> hasMatchingTag(outcome, tag));
    }

    private boolean hasMatchingTag(TestOutcome outcome, TestTag tag) {
        Optional<Boolean> cachedMatch = TestTagCache.hasMatchingTag(outcome, tag);

        if (cachedMatch.isPresent()) {
            return cachedMatch.get();
        } else {
            boolean matchFound = isAnIssue(tag) ? outcome.hasIssue(tag.getName()) : outcome.hasTag(tag) || outcome.hasAMoreGeneralFormOfTag(tag);
            TestTagCache.storeMatchingTagResult(outcome, tag, matchFound);
            return matchFound;
        }
    }

    private boolean isAnIssue(TestTag tag) {
        return tag.getType().equalsIgnoreCase("issue");
    }

    public String getResultFilterName() {
        return (resultFilter != null) ? resultFilter.name() : "";
    }

    public TestOutcomes getUnsuccessfulTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ERROR, TestResult.FAILURE,  TestResult.COMPROMISED))
                .withLabel(labelForTestsWithStatus("unsuccessful tests"))
                .withResultFilter(TestResult.UNSUCCESSFUL)
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
                .withResultFilter(TestResult.FAILURE)
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getAbortedTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ABORTED))
                .withLabel(labelForTestsWithStatus("aborted tests"))
                .withResultFilter(TestResult.ABORTED)
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getErrorTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.ERROR))
                .withLabel(labelForTestsWithStatus("tests with errors"))
                .withResultFilter(TestResult.ERROR)
                .withRootOutcomes(getRootOutcomes());
    }

    public TestOutcomes getCompromisedTests() {
        return TestOutcomes.of(outcomesFilteredByResult(TestResult.COMPROMISED))
                .withLabel(labelForTestsWithStatus("compromised tests"))
                .withResultFilter(TestResult.COMPROMISED)
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
                .withResultFilter(TestResult.SUCCESS)
                .withRootOutcomes(getRootOutcomes());
    }

    /**
     * Find the pending or ignored test outcomes in this set
     *
     * @return A new set of test outcomes containing only the pending or ignored tests
     */
    public TestOutcomes getPendingTests() {

        List<TestOutcome> pendingOutcomes = outcomesWithResults(outcomes, TestResult.PENDING);
        return TestOutcomes.of(pendingOutcomes)
                .withLabel(labelForTestsWithStatus("pending tests"))
                .withResultFilter(TestResult.PENDING)
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

    public String getResultTypeLabel() {
        String resultTypeAdjective = (resultFilter != null) ? resultFilter.getAdjective().toLowerCase() + " " : "";

        return resultTypeAdjective + Inflector.inflection().of(getTotalMatchingScenarios()).times("test").inPluralForm().toString();
    }
    /**
     * @return The total number of test runs in this set (including rows in data-driven tests).
     */
    public int getTotal() {
        return outcomes.stream()
                .mapToInt(TestOutcome::getTestCount)
                .sum();
    }

    public int getTotalMatchingScenarios() {
        if (resultFilter == null) { return getTotal(); }

        return scenarioCountWithResult(resultFilter);
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
            return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR) + withResult(TestResult.COMPROMISED) + withResult(TestResult.ABORTED);
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
        return getScenarioCount();
    }

    /**
     * The test case count include all individual tests data-driven tests. A data-driven test counts as 1 test.
     * @return
     */
    public int getTestCaseCount() {
        return outcomes.size();
    }

    /**
     * The scenario count include all individual tests and rows in data-driven tests.
     * @return
     */
    public int getScenarioCount() {
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
            return matchers.stream().anyMatch( match -> match.matches(name));
        }
    }

    public boolean isEmpty() {
        return getOutcomes().isEmpty();
    }

    @Override
    public String toString() {
        return "TestOutcomes{" +
                "outcomes=" + outcomes +
                '}';
    }
}
