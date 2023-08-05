package net.thucydides.core.batches.integration;

import net.thucydides.model.batches.SystemVariableBasedBatchManager;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenCoordinatingTestBatches {

    MockEnvironmentVariables environmentVariables;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
    }
    @Test
    public void should_keep_a_counter_of_tests_being_executed() {
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        assertThat(batchManager.getCurrentTestCaseNumber(), is(0));

        batchManager.registerTestCase(WhenCoordinatingTestBatches.class);

        assertThat(batchManager.getCurrentTestCaseNumber(), is(1));

    }


    @Test
    public void by_default_all_tests_should_run() {
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        int executedTests = 0;
        for(int i = 0; i < 50; i++) {
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests++;
            }
        }

        assertThat(executedTests, is(50));
    }

    @Test
    public void should_flag_every_n_tests_to_be_run() {
        environmentVariables.setProperty("thucydides.batch.size", "10");
        environmentVariables.setProperty("thucydides.batch.number", "1");

        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        List<Integer> executedTests = new ArrayList<Integer>();
        for(int testNumber = 1; testNumber <= 50; testNumber++) {
            batchManager.registerTestCase("Test Case " + testNumber);
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests.add(testNumber);
            }
        }

        assertThat(executedTests.size(), is(5));
        assertThat(executedTests, hasItems(1,11,21,31,41));
    }
    @Test
    public void should_support_the_old_batch_count_property() {
        environmentVariables.setProperty("thucydides.batch.count", "10");
        environmentVariables.setProperty("thucydides.batch.number", "1");

        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        List<Integer> executedTests = new ArrayList<Integer>();
        for(int testNumber = 1; testNumber <= 50; testNumber++) {
            batchManager.registerTestCase("Test Case " + testNumber);
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests.add(testNumber);
            }
        }

        assertThat(executedTests.size(), is(5));
        assertThat(executedTests, hasItems(1,11,21,31,41));
    }

    @Test
    public void should_flag_every_2nd_tests_to_be_run() {
        environmentVariables.setProperty("thucydides.batch.size", "10");
        environmentVariables.setProperty("thucydides.batch.number", "2");

        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        List<Integer> executedTests = new ArrayList<Integer>();
        for(int testNumber = 1; testNumber <= 50; testNumber++) {
            batchManager.registerTestCase("Test Case " + testNumber);
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests.add(testNumber);
            }
        }

        assertThat(executedTests.size(), is(5));
        assertThat(executedTests, hasItems(2,12,22,32,42));
    }

    @Test
    public void should_handle_tests_being_run_in_parallel() {
        environmentVariables.setProperty("thucydides.batch.size", "10");

        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(environmentVariables);

        List<Integer> executedTests = new ArrayList<Integer>();
        for(int testNumber = 1; testNumber <= 50; testNumber++) {
            batchManager.registerTestCase("Test Case " + testNumber);
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests.add(testNumber);
            }
        }

        assertThat(executedTests.size(), is(5));
        assertThat(executedTests, hasItems(10,20,30,40,50));
    }


    public class TestRunnerThread extends Thread {

        List<Integer> executedTests = new ArrayList<Integer>();

        private final SystemVariableBasedBatchManager batchManager;

        public TestRunnerThread(final SystemVariableBasedBatchManager batchManager)  throws InitializationError {
            this.batchManager = batchManager;
            executedTests = new ArrayList<Integer>();
        }

        public void run() {
            runFiftyTestCases(batchManager, executedTests);
        }

        public List<Integer> getExecutedTests() {
            return executedTests;
        }
    }

    private final AtomicInteger counter = new AtomicInteger(1);
    private void runFiftyTestCases(SystemVariableBasedBatchManager batchManager, List<Integer> executedTests) {
        for(int testNumber = 0; testNumber < 50; testNumber++) {
            batchManager.registerTestCase("Test Case " + counter.getAndIncrement());
            if (batchManager.shouldExecuteThisTest(1)) {
                executedTests.add(testNumber);
            }
        }
    }

    @Test
    public void the_test_runner_executes_the_steps_in_batches() throws InitializationError, InterruptedException {

        List<List<Integer>> executedTestResults = new ArrayList<List<Integer>>();
        for(int i = 1; i <= 10; i++) {
            MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
            threadVariables.setProperty("thucydides.batch.count", "10");
            threadVariables.setProperty("thucydides.batch.number", Integer.toString(i));
            SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);
            List<Integer> executedTests = new ArrayList<Integer>();
            executedTestResults.add(executedTests);
            runFiftyTestCases(batchManager, executedTests);
        }

        assertThat(executedTestResults.get(0), hasItems(0,10,20,30,40));
        assertThat(executedTestResults.get(1), hasItems(1,11,21,31,41));
        assertThat(executedTestResults.get(2), hasItems(2,12,22,32,42));
        assertThat(executedTestResults.get(3), hasItems(3,13,23,33,43));
        assertThat(executedTestResults.get(4), hasItems(4,14,24,34,44));
        assertThat(executedTestResults.get(5), hasItems(5,15,25,35,45));
        assertThat(executedTestResults.get(6), hasItems(6,16,26,36,46));
        assertThat(executedTestResults.get(7), hasItems(7,17,27,37,47));
        assertThat(executedTestResults.get(8), hasItems(8,18,28,38,48));
        assertThat(executedTestResults.get(9), hasItems(9,19,29,39,49));
    }

    @Test
    public void the_test_runner_executes_the_steps_in_batches_when_tests_are_run_in_parallel() throws InitializationError, InterruptedException {

        List<TestRunnerThread> threads = new ArrayList<TestRunnerThread>();

        for(int i = 1; i <= 10; i++) {
            MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
            threadVariables.setProperty("thucydides.batch.count", "10");
            threadVariables.setProperty("thucydides.batch.number", Integer.toString(i));
            SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);
            threads.add(new TestRunnerThread(batchManager));
        }

        for(TestRunnerThread thread : threads) {
            thread.start();
        }

        for(TestRunnerThread thread : threads) {
            thread.join();
        }

        assertThat(threads.get(0).getExecutedTests(), hasItems(0,10,20,30,40));
        assertThat(threads.get(1).getExecutedTests(), hasItems(1,11,21,31,41));
        assertThat(threads.get(2).getExecutedTests(), hasItems(2,12,22,32,42));
        assertThat(threads.get(3).getExecutedTests(), hasItems(3,13,23,33,43));
        assertThat(threads.get(4).getExecutedTests(), hasItems(4,14,24,34,44));
        assertThat(threads.get(5).getExecutedTests(), hasItems(5,15,25,35,45));
        assertThat(threads.get(6).getExecutedTests(), hasItems(6,16,26,36,46));
        assertThat(threads.get(7).getExecutedTests(), hasItems(7,17,27,37,47));
        assertThat(threads.get(8).getExecutedTests(), hasItems(8,18,28,38,48));
        assertThat(threads.get(9).getExecutedTests(), hasItems(9,19,29,39,49));
    }
}
