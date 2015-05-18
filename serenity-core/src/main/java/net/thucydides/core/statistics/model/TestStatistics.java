package net.thucydides.core.statistics.model;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.TestResult;

import java.util.List;

import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.is;

/**
 * A summary of statistics related to a particular set of tests.
 * First, obtain a set of statistics using the HibernateTestStatisticsProvider class. For example, you could
 * obtain statistics for a particular test outcome like this:
 * <pre>
 *     <code>
 *      TestStatistics stats = testStatisticsProvider.statisticsForTests(With.title(testOutcome.getTitle()));
 *     </code>
 * </pre>
 * or
 * <pre>
 *     <code>
 *      TestStatistics stats = testStatisticsProvider.statisticsForTests(With.tag("A story));
 *     </code>
 * </pre>
 * Then, you can obtain various statistics about the test (or group of tests):
 * <pre>
 *     <code>
 *     Double passRateForAllTests = stats.getOverallPassRate();
 *     Double recentPassRate = stats.getPassRate().overTheLast(5).testRuns();
 *     </code>
 * </pre>
 */
public class TestStatistics {

    private final Long totalTestRuns;
    private final Long passingTestRuns;
    private final Long failingTestRuns;
    private final List<TestResult> testResults;
    private final List<TestRunTag> tags;
    private static final int OVERALL = Integer.MAX_VALUE;

    public TestStatistics(Long totalTestRuns,
                          Long passingTestRuns,
                          Long failingTestRuns,
                          List<TestResult> testResults,
                          List<TestRunTag> tags) {
        this.totalTestRuns = totalTestRuns;
        this.passingTestRuns = passingTestRuns;
        this.failingTestRuns = failingTestRuns;
        this.tags = ImmutableList.copyOf(tags);
        this.testResults = ImmutableList.copyOf(testResults);
    }

    public Long getTotalTestRuns() {
        return totalTestRuns;
    }

    public Long getPassingTestRuns() {
        return passingTestRuns;
    }

    public Long getFailingTestRuns() {
        return failingTestRuns;
    }

    public Double getOverallPassRate() {
        if (totalTestRuns > 0) {
            return (double) passingTestRuns / (double) totalTestRuns;
        } else {
            return 0.0;
        }
    }

    public List<TestRunTag> getTags() {
        return tags;
    }

    /**
     * Find the pass rate over a given number of tests
     * <pre>
     *     <code>
     *     Double recentPassRate = stats.getPassRate().overTheLast(5).testRuns();
     *     </code>
     * </pre>
     * @return the pass rate over a given number of tests
     */
    public PassRateBuilder getPassRate() {
        return new PassRateBuilder(OVERALL);
    }

    public ResultCountBuilder countResults() {
        return new ResultCountBuilder(OVERALL);
    }

    public class PassRateBuilder {

        int testRunsOverPeriod;

        public PassRateBuilder(int testRunsOverPeriod) {
            this.testRunsOverPeriod = testRunsOverPeriod;
        }

        public PassRateBuilder overTheLast(int number) {
            return new PassRateBuilder(number);
        }

        public double testRuns() {
            int successfulRecentTestRuns = countSuccessfulTestRunsInLast(testRunsOverPeriod, testResults);
            int eligableTestRunCount = (testResults.size() < testRunsOverPeriod) ? testResults.size() : testRunsOverPeriod;
            if (eligableTestRunCount > 0) {
                return (successfulRecentTestRuns * 1.0) / (eligableTestRunCount * 1.0);
            } else {
                return 0.0;
            }
        }

        private int countSuccessfulTestRunsInLast(int testRunCount, List<TestResult> testResults) {
            List<TestResult> eligableTestResults = mostRecent(testRunCount, testResults);
            List<TestResult> successfulTestResults = select(eligableTestResults, is(TestResult.SUCCESS));
            return successfulTestResults.size();
        }

        private List<TestResult> mostRecent(int testRunsOverPeriod, List<TestResult> testResults) {
            int eligableCount = eligableTestResultSize(testResults, testRunsOverPeriod);
            return testResults.subList(0, eligableCount);
        }

        private int eligableTestResultSize(List<TestResult> testResults, int testRunsOverPeriod) {
            return (testRunsOverPeriod > testResults.size()) ? testResults.size() : testRunsOverPeriod;
        }

    }

    public class ResultCountBuilder {

        int testRunsOverPeriod;

        public ResultCountBuilder(int testRunsOverPeriod) {
            this.testRunsOverPeriod = testRunsOverPeriod;
        }

        public ResultCountBuilder overTheLast(int number) {
            return new ResultCountBuilder(number);
        }

        public int whereTheOutcomeWas(TestResult testResult) {
            return countTestRunsByResultInLast(testResult, testRunsOverPeriod, testResults);
        }

        private int countTestRunsByResultInLast(TestResult testResult, int testRunCount, List<TestResult> testResults) {
            List<TestResult> eligableTestResults = mostRecent(testRunCount, testResults);
            List<TestResult> successfulTestResults = select(eligableTestResults, is(testResult));
            return successfulTestResults.size();
        }

        private List<TestResult> mostRecent(int testRunsOverPeriod, List<TestResult> testResults) {
            int eligableCount = eligableTestResultSize(testResults, testRunsOverPeriod);
            return testResults.subList(0, eligableCount);
        }

        private int eligableTestResultSize(List<TestResult> testResults, int testRunsOverPeriod) {
            return (testRunsOverPeriod > testResults.size()) ? testResults.size() : testRunsOverPeriod;
        }
    }
}
