package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.annotations.Steps;
import net.thucydides.model.batches.SystemVariableBasedBatchManager;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.samples.SampleNonWebSteps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class RunningTestScenariosInBatches {


    static List<Integer> executedTests = new CopyOnWriteArrayList<Integer>();
    static AtomicInteger testCount = new AtomicInteger(0);

    private void runTestCases(SystemVariableBasedBatchManager batchManager) {
        try {
            new SerenityRunner(SampleTestScenario1.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario2.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario3.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario4.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario5.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario6.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario7.class, batchManager).run(new RunNotifier());
            new SerenityRunner(SampleTestScenario8.class, batchManager).run(new RunNotifier());
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
        }
    }

    @Before
    public void clearCounters() {
        executedTests.clear();
    }

    @Test
    public void the_thread_for_batch_1_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(1));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(3));
        assertThat(executedTests, hasItems(1, 4, 7));
    }

    @Test
    public void the_thread_for_batch_2_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(2));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(3));
        assertThat(executedTests, hasItems(2, 5, 8));
    }


    @Test
    public void the_thread_for_batch_3_should_only_run_tests_in_that_batch() throws InitializationError, InterruptedException {

        MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
        threadVariables.setProperty("thucydides.batch.count", "3");
        threadVariables.setProperty("thucydides.batch.number", Integer.toString(3));
        SystemVariableBasedBatchManager batchManager = new SystemVariableBasedBatchManager(threadVariables);

        runTestCases(batchManager);

        assertThat(executedTests.size(), is(2));
        assertThat(executedTests, hasItems(3, 6));
    }
    // TEST CLASSES USED IN THE MAIN TESTS.

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario1 {

        int testNumber;

        public SampleTestScenario1() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(1);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario2 {

        int testNumber;

        public SampleTestScenario2() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(2);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario3 {

        int testNumber;

        public SampleTestScenario3() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(3);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }


    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario4 {

        int testNumber;

        public SampleTestScenario4() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(4);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }



    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario5 {

        int testNumber;

        public SampleTestScenario5() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(5);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario6 {

        int testNumber;

        public SampleTestScenario6() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(6);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario7 {

        int testNumber;

        public SampleTestScenario7() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(7);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }

    @RunWith(SerenityRunner.class)
    public static class SampleTestScenario8 {

        int testNumber;

        public SampleTestScenario8() {
            this.testNumber = testCount.getAndIncrement();
        }

        @Steps
        public SampleNonWebSteps steps;

        @Test
        public void happy_day_scenario() {

            executedTests.add(8);

            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

        @Test
        public void yet_another_happy_day_scenario() {
            steps.stepThatSucceeds();
            steps.stepThatSucceeds();
            steps.anotherStepThatSucceeds();
        }

    }
}

