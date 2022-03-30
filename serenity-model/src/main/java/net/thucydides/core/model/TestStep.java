package net.thucydides.core.model;

import com.google.common.base.Objects;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.rest.RestQuery;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.model.screenshots.Screenshot;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.model.stacktrace.RootCauseAnalyzer;
import net.thucydides.core.requirements.reports.CompoundDuration;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
//import org.joda.time.DateTime;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.thucydides.core.model.TestResult.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * An acceptance test run is made up of test steps.
 * Test steps can be either concrete steps or groups of steps.
 * Each concrete step should represent an action by the user, and (generally) an expected outcome.
 * A test step is described by a narrative-style phrase (e.g. "the user clicks
 * on the 'Search' button', "the user fills in the registration form', etc.).
 * A screenshot is stored for each step.
 *
 * @author johnsmart
 */
public class TestStep implements Cloneable {

    public static final TestStep NO_STEP = new TestStep("NO STEP");
    private int number;
    private String description;
    private long duration;
    private ZonedDateTime startTime;
    private List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();
    private FailureCause exception;
    private TestResult result;
    private RestQuery restQuery;
    private List<ReportData> reportData;
    private boolean precondition;
    private int level;
    private Integer lineNumber;
    private ExternalLink externalLink;
    private Boolean manual;

    public final static Predicate<TestStep> IGNORED_TESTSTEPS = testStep -> testStep.getResult() == IGNORED;
    public final static Predicate<TestStep> COMPROMISED_TESTSTEPS = testStep -> testStep.getResult() == COMPROMISED;
    public final static Predicate<TestStep> SUCCESSFUL_TESTSTEPS = testStep -> testStep.getResult() == SUCCESS;
    public final static Predicate<TestStep> FAILING_TESTSTEPS = testStep -> testStep.getResult() == FAILURE;
    public final static Predicate<TestStep> ERROR_TESTSTEPS = testStep -> testStep.getResult() == ERROR;
    public final static Predicate<TestStep> SKIPPED_TESTSTEPS = testStep -> testStep.getResult() == SKIPPED;


    private List<TestStep> children = new ArrayList<>();

    public TestStep() {
        startTime = now();
    }

    protected void setNumber(int number) {
        this.number = number;
    }

    private SystemClock getSystemClock() {
        return Injectors.getInjector().getInstance(SystemClock.class);
    }

    private ZonedDateTime now() {
        return getSystemClock().getCurrentTime();
    }

    public static TestStepBuilder forStepCalled(String description) {
        return new TestStepBuilder(description);
    }

    public boolean hasScreenshots() {
        return !getScreenshots().isEmpty();
    }

    protected List<TestStep> children() {
        return children;
    }

    public int renumberFrom(int count) {
        setNumber(count++);
        if (hasChildren()) {
            count = renumberChildrenFrom(count);
        }
        return count;
    }

    private int renumberChildrenFrom(int count) {
        for (TestStep step : children) {
            count = step.renumberFrom(count);
        }
        return count;
    }

    public void recordRestQuery(RestQuery restQuery) {
        this.restQuery = restQuery;
    }

    public void updateOverallResult() {
        if (result != null) {
            if (getResultFromChildren().overrides(result)) {
                result = null;
            }
        }
    }

    public void setPrecondition(boolean precondition) {
        this.precondition = precondition;
    }

    public TestStep withResult(TestResult annotatedResult) {
        TestStep annotatedStep = this.clone();
        annotatedStep.result = annotatedResult;
        return annotatedStep;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean correspondsToLine(int lineNumber) {
        return (this.lineNumber != null) && (this.lineNumber == lineNumber);
    }

    public ExternalLink getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(ExternalLink externalLink) {
        this.externalLink = externalLink;
    }

    public static class TestStepBuilder {
        private final String description;

        public TestStepBuilder(String description) {
            this.description = description;
        }

        public TestStep withResult(TestResult result) {
            TestStep step = new TestStep(description);
            step.setResult(result);
            return step;
        }
    }

    @Override
    public String toString() {
        if (!hasChildren()) {
            return description;
        } else {
            String childDescriptions = children.stream().map(TestStep::toString).collect(Collectors.joining(", "));
            return description + " [" + childDescriptions + "]";
        }
    }

    public TestStep(final String description) {
        this();
        this.description = description;
        this.level = 0;
    }

    public TestStep(final ZonedDateTime startTime, final String description) {
        this();
        this.startTime = startTime;
        this.description = description;
        this.level = 0;
    }

//    @Deprecated
//    public TestStep(final DateTime startTime, final String description) {
//        this();
//
//        ZonedDateTime time =
//                ZonedDateTime.of(startTime.year().get(),
//                        startTime.monthOfYear().get(),
//                        startTime.dayOfMonth().get(),
//                        startTime.hourOfDay().get(),
//                        startTime.minuteOfHour().get(),
//                        startTime.secondOfMinute().get(),
//                        startTime.millisOfSecond().get() * 1000,
//                        ZoneId.systemDefault());
//
//        this.startTime = time;
//        this.description = description;
//        this.level = 0;
//    }


    public TestStep startingAt(ZonedDateTime time) {
        TestStep newTestStep = copyOfThisTestStep();
        newTestStep.startTime = time;
        return newTestStep;
    }


    @Override
    public TestStep clone() {
        TestStep newTestStep = new TestStep();
        newTestStep.description = description;
        newTestStep.startTime = startTime;
        newTestStep.duration = duration;
        newTestStep.screenshots = NewList.copyOf(screenshots);
        newTestStep.exception = exception;
        newTestStep.result = result;
        newTestStep.number = number;
        newTestStep.children = NewList.copyOf(children);
        newTestStep.precondition = precondition;
        newTestStep.level = level;
        return newTestStep;
    }

    protected TestStep copyOfThisTestStep() {
        return this.clone();
    }


    public void recordDuration() {
        setDuration(
                ChronoUnit.MILLIS.between(startTime, now())
        );
    }

    public int getNumber() {
        return number;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TestStep unrendered() {
        TestStep stepCopy = clone();
        stepCopy.setDescription(stripMarkupFrom(description));
        return stepCopy;
    }

    private String stripMarkupFrom(String description) {
        return Jsoup.parse(description).text();
    }

    public List<TestStep> getChildren() {
        return new ArrayList(children);
    }

    public List<ScreenshotAndHtmlSource> getScreenshots() {
        return new ArrayList(screenshots);
    }

    public List<ScreenshotAndHtmlSource> getAllScreenshots() {

        Set<ScreenshotAndHtmlSource> allScreenshots
                                                = children.stream()
                                                          .flatMap( child -> child.getAllScreenshots().stream() )
                                                          .collect(Collectors.toSet());

        allScreenshots.addAll(screenshots);


        List<ScreenshotAndHtmlSource> screenshotInOrderOfAppearance = new ArrayList<>(screenshots);
        screenshotInOrderOfAppearance.sort(Comparator.comparingLong(ScreenshotAndHtmlSource::getTimeStamp));

        return screenshotInOrderOfAppearance;
    }

    /**
     * Returns rendered screenshots of this step and of child steps, in order of appearance,
     * and starting with the earliet screenshot overall with the name of this step.
     */
    public List<Screenshot> getRenderedScreenshots() {
        List<Screenshot> stepScreenshots = (screenshots == null) ? new ArrayList<>()
                : screenshots.stream()
                             .map(screenshot -> renderedScreenshotOf(screenshot, this.getLevel())).collect(Collectors.toList());

        children.forEach(
                child -> stepScreenshots.addAll(child.getRenderedScreenshots())
        );

        stepScreenshots.sort(Comparator.comparingLong(Screenshot::getTimestamp));

        if (!stepScreenshots.isEmpty()) {
            Screenshot earliestScreenshot = stepScreenshots.get(0);
            Screenshot lastScreenshot = stepScreenshots.get(stepScreenshots.size() - 1);

            if (isScreenshotInThisStep(lastScreenshot) && lastScreenshot.getTimestamp() > earliestScreenshot.getTimestamp()) {
                stepScreenshots.remove(stepScreenshots.size() - 1);
                stepScreenshots.add(withClosingDescriptionForThisStep(lastScreenshot.withDepth(level)));
            }

            if (thereAreNoScreenshotsInThisStep() || !isScreenshotInThisStep(earliestScreenshot)) {
                stepScreenshots.add(0, withDescriptionForThisStep(earliestScreenshot.withDepth(level)));
            }
        }

        return stepScreenshots;
    }

    private Screenshot withClosingDescriptionForThisStep(Screenshot screenshot) {
        return screenshot.withDescription(screenshot.getDescription() + " (completed)");
    }

    private Screenshot withDescriptionForThisStep(Screenshot screenshot) {
        return screenshot.before().withDescription(getDescription());
    }

    private boolean isScreenshotInThisStep(Screenshot screenshot) {
        return screenshot.getDescription().equals(getDescription());
    }

    private boolean thereAreNoScreenshotsInThisStep() {
        return  (screenshots == null) || screenshots.isEmpty();
    }

    public List<Screenshot> getTopLevelScreenshots() {
        if (screenshots == null) { return new ArrayList<>(); }

        return screenshots.stream().map(screenshot -> renderedScreenshotOf(screenshot, level)).collect(Collectors.toList());
    }

    public  Screenshot renderedScreenshotOf(ScreenshotAndHtmlSource from, int level) {
        return new Screenshot(from.getScreenshot().getName(),
                getDescription(),
                widthOf(from.getScreenshot()),
                from.getTimeStamp(),
                getException(),
                level);
    }

    private static int widthOf(final File screenshot) {
        try {
            return new ResizableImage(screenshot).getWidth();
        } catch (IOException e) {
            return ThucydidesSystemProperty.DEFAULT_WIDTH;
        }
    }

    private <T> T firstOf(List<T> elements) {
        return elements.get(0);
    }

    private <T> T  lastOf(List<T> elements) {
        return elements.get(elements.size() - 1);
    }


    public boolean hasRestQuery() {
        return restQuery != null;
    }

    public boolean hasData() {
        return reportData != null && ! reportData.isEmpty();
    }

    public RestQuery getRestQuery() {
        return restQuery;
    }

    public int getLevel() {
        return level;
    }

    public List<ReportData> getReportEvidence() {
        return getReportData().stream().filter(ReportData::isEvidence).collect(Collectors.toList());
    }

    public List<ReportData> getReportData() {
        return (reportData == null) ? new ArrayList<>() : reportData;
    }

    public ScreenshotAndHtmlSource getFirstScreenshot() {
        if ((screenshots != null) && (!screenshots.isEmpty())) {
            return screenshots.get(0);
        } else {
            return null;
        }
    }

    public Screenshot getEarliestScreenshot() {

        List<Screenshot> screenshots = getRenderedScreenshots();

        if (screenshots.isEmpty()) { return null; }
        return screenshots.get(0);
    }



    public Screenshot getLatestScreenshot() {
        List<Screenshot> screenshots = getRenderedScreenshots();
        if (screenshots.isEmpty()) { return null; }
        return screenshots.get(screenshots.size() - 1);
    }

    public ScreenshotAndHtmlSource getLastScreenshot() {
        if ((screenshots != null) && (!screenshots.isEmpty())) {
            return screenshots.get(screenshots.size() - 1);
        } else {
            return null;
        }
    }

    public boolean needsScreenshots() {
        return (!isAGroup() && getScreenshots() != null);
    }

    /**
     * Each test step has a result, indicating the outcome of this step.
     *
     * @param result The test outcome associated with this step.
     */
    public void setResult(final TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        if (isManual()) {
            return getResultFromThisStep();
        }
        if (isAGroup() && !groupResultOverridesChildren()) {
            return (result != null) ? TestResultComparison.overallResultFor(result, getResultFromChildren()) : getResultFromChildren();
        } else {
            return getResultFromThisStep();
        }
    }

    private boolean isManual() {
        return manual != null && manual;
    }

    public TestStep asManual() {
        manual = true;
        return this;
    }

    private TestResult getResultFromThisStep() {
        if (result == null) {
            return SUCCESS;
        }
        return result;
    }

    private boolean groupResultOverridesChildren() {
        return ((result == SKIPPED) || (result == IGNORED) || (result == PENDING));
    }

    private TestResult getResultFromChildren() {
        return TestResultList.overallResultFrom(getChildResults());
    }


    private List<TestResult> getChildResults() {
        List<TestResult> childResults = new ArrayList<>();
        for (TestStep step : getChildren()) {
            childResults.add(step.getResult());
        }
        return childResults;
    }

    public Boolean isSuccessful() {
        return getResult() == SUCCESS;
    }

    public Boolean isFailure() {
        return getResult() == FAILURE;
    }

    public Boolean isError() {
        return getResult() == ERROR;
    }

    public Boolean isCompromised() {
        return getResult() == COMPROMISED;
    }

    public Boolean isIgnored() {
        return getResult() == IGNORED;
    }

    public Boolean isSkipped() {
        return getResult() == SKIPPED;
    }

    public Boolean isPending() {
        return getResult() == PENDING;
    }

    public boolean isAPrecondition() {
        return precondition;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public double getDurationInSeconds() {
        return TestDuration.of(duration).inSeconds();
    }

    public String getFormattedDuration() {
        return  (duration != 0L) ? "" + CompoundDuration.of(duration) : "";
    }

    /**
     * Indicate that this step failed with a given error.
     *
     * @param cause why the test failed.
     */
    public void failedWith(final Throwable cause) {
        this.exception = new RootCauseAnalyzer(cause).getRootCause();
        setResult(new FailureAnalysis().resultFor(this.exception.toException()));
    }

    public String getError() {
        return (exception != null) ? exception.getMessage() : "";
    }


    public String getErrorMessage() {
        if (exception == null) {
            return "";
        }
        return (isEmpty(exception.getMessage())) ?
                exception.getErrorType() :
                exception.getErrorType() + ": " + exception.getMessage();
    }

    public String getConciseErrorMessage() {
        if (exception == null) {
            return "";
        }
        return (isEmpty(exception.getMessage())) ?
                exception.getSimpleErrorType() :
                exception.getSimpleErrorType() + ": " + exception.getShortenedMessage();
    }

    /**
     * The test has been aborted (marked as pending or ignored) for a reason described in the exception.
     */
    public void testAborted(final Throwable exception) {
        this.exception = new RootCauseAnalyzer(exception).getRootCause();
    }

    public String getShortErrorMessage() {
        return new ErrorMessageFormatter(getErrorMessage()).getShortErrorMessage();
    }

    public FailureCause getException() {
        return exception;
    }

    public void clearException() {
        this.exception = null;
    }

    public FailureCause getNestedException() {
        for (TestStep step : getFlattenedSteps()) {
            if (step.getException() != null) {
                return step.getException();
            }
        }
        return getException();
    }

    public List<? extends TestStep> getFlattenedSteps() {
        List<TestStep> flattenedSteps = new ArrayList<>();
        for (TestStep child : getChildren()) {
            flattenedSteps.add(child);
            if (child.isAGroup()) {
                flattenedSteps.addAll(child.getFlattenedSteps());
            }
        }

        flattenedSteps.sort(Comparator.comparingLong(TestStep::getNumber));

        return flattenedSteps;
    }

    private TestStep withLevel(int level) {
        this.level = level;
        return this;
    }

    public boolean isAGroup() {
        return hasChildren();
    }

    public boolean hasNestedErrors() {
        for (TestStep child : getFlattenedSteps()) {
            if (child.isFailure() || child.isError()) {
                return true;
            }
        }
        return false;
    }

    public TestStep addChildStep(final TestStep step) {
        children.add(step.withLevel(level + 1));
        return this;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasMultipleScreenshots() {
        Set<String> uniqueScreenshots = getRenderedScreenshots().stream()
                .map(Screenshot::getFilename)
                .collect(Collectors.toSet());

        return uniqueScreenshots.size() > 1;
    }

    public Collection<? extends TestStep> getLeafTestSteps() {
        List<TestStep> leafSteps = new ArrayList<>();
        for (TestStep child : getChildren()) {
            if (child.isAGroup()) {
                leafSteps.addAll(child.getLeafTestSteps());
            } else {
                leafSteps.add(child);
            }
        }
        return leafSteps;
    }

    public TestStep addScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (thisIsANew(screenshotAndHtmlSource)) {
            screenshots.add(screenshotAndHtmlSource);
        }
        return this;
    }

    private boolean thisIsANew(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (screenshots.isEmpty()) {
            return true;
        } else {
            ScreenshotAndHtmlSource previousScreenshot = screenshots.get(screenshots.size() - 1);
            return !screenshotAndHtmlSource.getScreenshotName().equals(previousScreenshot.getScreenshotName());
        }
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public int getActualScreenshotCount() {
        int screenshotCount = 0;
        if(hasChildren()){
            for(TestStep step:children){
                screenshotCount +=  step.getActualScreenshotCount()+1;
            }
            if(hasScreenshots()) screenshotCount += 1;
            return screenshotCount;
        }else{
            return getScreenshotCount() - 1;
        }
    }

    public int getScreenshotCount() {
        return screenshots.size();
    }

    public void removeScreenshot(int index) {
        screenshots.remove(index);
    }

    public TestStep withReportData(ReportData reportData) {
        if (this.reportData == null) {
            this.reportData = new ArrayList<>();
        }
        this.reportData.add(reportData);
        return this;
    }

    public TestStep recordReportData(ReportData reportData) {
        return this.withReportData(reportData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestStep)) return false;

        TestStep testStep = (TestStep) o;

        if (duration != testStep.duration) return false;
        if (number != testStep.number) return false;
        if (!startTime.equals(testStep.startTime)) return false;
        // TODO
//        if (exception != null ? !exceptionsAreEqual(exception, testStep.exception) : testStep.exception != null) return false;
        if (!children.equals(testStep.children)) return false;
        if (!description.equals(testStep.description)) return false;
        if (result != testStep.result) return false;
        return !(screenshots != null ? !screenshots.equals(testStep.screenshots) : testStep.screenshots != null);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number, description, duration, startTime, screenshots, exception, result, restQuery, precondition, children);
    }
}
