package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static net.thucydides.core.model.TestResult.*;

/**
 * An acceptance test run is made up of test steps.
 * Test steps can be either concrete steps or groups of steps.
 * Each concrete step should represent an action by the user, and (generally) an expected outcome.
 * A test step is described by a narrative-style phrase (e.g. "the user clicks 
 * on the 'Search' button', "the user fills in the registration form', etc.).
 * A screenshot is stored for each step.
 * 
 * @author johnsmart
 *
 */
public class TestStep {

    private int number;
    private String description;    
    private long duration;
    private long startTime;
    private List<ScreenshotAndHtmlSource> screenshots = new ArrayList<>();
    private FailureCause exception;
    private TestResult result;

    private List<TestStep> children = new ArrayList<>();

    public TestStep() {
        startTime = now().getMillis();
    }

    protected void setNumber(int number) {
        this.number = number;
    }

    private SystemClock getSystemClock() {
        return Injectors.getInjector().getInstance(SystemClock.class);
    }

    private DateTime now() {
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
        for(TestStep step :children) {
            count = step.renumberFrom(count);
        }
        return count;
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
            String childDescriptions = join(extract(children, on(TestStep.class).toString()));
            return description + " [" + childDescriptions + "]";
        }
    }

    public TestStep(final String description) {
        this();
        this.description = description;
    }

    public TestStep(final DateTime startTime, final String description) {
        this();
        this.startTime = startTime.getMillis();
        this.description = description;
    }

    public TestStep startingAt(DateTime time) {
        TestStep newTestStep = copyOfThisTestStep();
        newTestStep.startTime = time.getMillis();
        return newTestStep;
    }


    @Override
    public TestStep clone() {
        TestStep newTestStep = new TestStep();
        newTestStep.description = description;
        newTestStep.startTime = startTime;
        newTestStep.duration = duration;
        newTestStep.screenshots = Lists.newArrayList(screenshots);
        newTestStep.exception = exception;
        newTestStep.result = result;
        newTestStep.number = number;
        newTestStep.children = Lists.newArrayList(children);
        return newTestStep;
    }

    protected TestStep copyOfThisTestStep() {
        return this.clone();
    }


    public void recordDuration() {
        setDuration(now().getMillis() - startTime);
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

    public List<TestStep> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public List<ScreenshotAndHtmlSource> getScreenshots() {
        return ImmutableList.copyOf(screenshots);
    }



    public ScreenshotAndHtmlSource getFirstScreenshot() {
        if ((screenshots != null) && (!screenshots.isEmpty())) {
            return screenshots.get(0);
        } else {
            return null;
        }
    }

    public boolean needsScreenshots() {
        return (!isAGroup() && getScreenshots() != null);
    }
    /**
     * Each test step has a result, indicating the outcome of this step.
     * @param result The test outcome associated with this step.
     */
    public void setResult(final TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        if (isAGroup() && !groupResultOverridesChildren()) {
            return (result != null) ? TestResultList.of(result, getResultFromChildren()).getOverallResult() : getResultFromChildren();
            //return getResultFromChildren();
        } else {
            return getResultFromThisStep();
        }
    }

    private TestResult getResultFromThisStep() {
        if (result != null) {
            return result;
        } else {
            return TestResult.PENDING;
        }
    }

    private boolean groupResultOverridesChildren() {
        return ((result == SKIPPED) || (result == IGNORED) || (result == PENDING));
    }

    private TestResult getResultFromChildren() {
        TestResultList resultList = TestResultList.of(getChildResults());
        return resultList.getOverallResult();
    }

    private List<TestResult> getChildResults() {
        List<TestResult> results = new ArrayList<>();
        for (TestStep step : getChildren()) {
            results.add(step.getResult());
        }
        return results;
    }

    public Boolean isSuccessful() {
        return getResult() == SUCCESS;
    }

    public Boolean isFailure() {
        return  getResult() == FAILURE;
    }

    public Boolean isError() {
        return getResult() == ERROR;
    }

    public Boolean isIgnored() {
        return  getResult() == IGNORED;
    }

    public Boolean isSkipped() {
        return  getResult() == SKIPPED;
    }

    public Boolean isPending() {
        return  getResult() == PENDING;
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

    /**
     * Indicate that this step failed with a given error.
     * @param cause why the test failed.
     */
    public void failedWith(final Throwable cause) {
        this.exception = new RootCauseAnalyzer(cause).getRootCause();
        setResult(new FailureAnalysis().resultFor(this.exception.toException()));
    }

    public String getErrorMessage() {
        return (exception != null) ? exception.getMessage() : "";
    }

    /**
     * The test has been aborted (marked as pending or ignored) for a reason described in the exception.
     */
    public void testAborted(final Throwable exception) {
        new RootCauseAnalyzer(exception).getRootCause();
    }

    private String errorMessageFrom(final Throwable error) {
        return (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage();
    }

    public String getShortErrorMessage() {
        return new ErrorMessageFormatter(getErrorMessage()).getShortErrorMessage();
    }

    public FailureCause getException() {
        return exception;
    }

    public List<? extends TestStep> getFlattenedSteps() {
        List<TestStep> flattenedSteps = new ArrayList<>();
        for(TestStep child : getChildren()) {
            flattenedSteps.add(child);
            if (child.isAGroup()) {
                flattenedSteps.addAll(child.getFlattenedSteps());
            }
        }
        return flattenedSteps;
    }
    
    public boolean isAGroup() {
        return hasChildren();
    }

    public TestStep addChildStep(final TestStep step) {
        children.add(step);
        return this;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Collection<? extends TestStep> getLeafTestSteps() {
        List<TestStep> leafSteps = new ArrayList<>();
        for(TestStep child : getChildren()) {
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
            ScreenshotAndHtmlSource latestScreenshotAndHtmlSource = screenshots.get(screenshots.size() - 1);
            return !latestScreenshotAndHtmlSource.equals(screenshotAndHtmlSource);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public int getScreenshotCount() {
        return screenshots.size();
    }

    public void removeScreenshot(int index) {
        screenshots.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestStep)) return false;

        TestStep testStep = (TestStep) o;

        if (duration != testStep.duration) return false;
        if (number != testStep.number) return false;
        if (startTime != testStep.startTime) return false;
        // TODO
//        if (exception != null ? !exceptionsAreEqual(exception, testStep.exception) : testStep.exception != null) return false;
        if (!children.equals(testStep.children)) return false;
        if (!description.equals(testStep.description)) return false;
        if (result != testStep.result) return false;
        return !(screenshots != null ? !screenshots.equals(testStep.screenshots) : testStep.screenshots != null);

    }

    private boolean exceptionsAreEqual(Throwable exception, Throwable otherException) {
        if ((exception == null) && (otherException == null)) {
            return true;
        }
        return (StringUtils.equals(exception.getMessage(), otherException.getMessage())
                && (exceptionsAreEqual(exception.getCause(), otherException.getCause())));
    }

    @Override
    public int hashCode() {
        int result1 = number;
        result1 = 31 * result1 + description.hashCode();
        result1 = 31 * result1 + (int) (duration ^ (duration >>> 32));
        result1 = 31 * result1 + (int) (startTime ^ (startTime >>> 32));
        result1 = 31 * result1 + (screenshots != null ? screenshots.hashCode() : 0);
        result1 = 31 * result1 + (exception != null ? exception.hashCode() : 0);
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + children.hashCode();
        return result1;
    }
}
