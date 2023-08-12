package net.thucydides.core.batches.integration;

import net.thucydides.model.batches.TestCountBasedBatchManager;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenUseTestCountBasedBatchStrategy {
	private MockEnvironmentVariables environmentVariables;
	private List<Integer> testCountInTestCase;
	private final AtomicInteger counter = new AtomicInteger(1);

	@Before
	public void initMocks() {
		environmentVariables = new MockEnvironmentVariables();
		testCountInTestCase = Arrays.asList(3, 8, 1, 9, 1, 2, 1);
	}

	@Test
	public void should_keep_a_counter_of_tests_being_executed() {
		TestCountBasedBatchManager batchManager = new TestCountBasedBatchManager(environmentVariables);

		assertThat(batchManager.getCurrentTestCaseNumber(), is(0));

		batchManager.registerTestCase(WhenUseTestCountBasedBatchStrategy.class);

		assertThat(batchManager.getCurrentTestCaseNumber(), is(1));
	}

	@Test
	public void by_default_all_tests_should_run() {
		TestCountBasedBatchManager batchManager = new TestCountBasedBatchManager(environmentVariables);

		int executedTests = 0;
		for (int i = 0; i < 50; i++) {
			if (batchManager.shouldExecuteThisTest(i)) {
				executedTests++;
			}
		}

		assertThat(executedTests, is(50));
	}

	@Test
	public void should_split_tests_between_batches_optimally() {
		List<List<Integer>> executedTestResults = new ArrayList<List<Integer>>();
		for (int i = 1; i <= 3; i++) {
			MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
			threadVariables.setProperty("thucydides.batch.count", "3");
			threadVariables.setProperty("thucydides.batch.number", Integer.toString(i));
			TestCountBasedBatchManager batchManager = new TestCountBasedBatchManager(threadVariables);
			List<Integer> executedTests = new ArrayList<Integer>();
			executedTestResults.add(executedTests);
			runPreparedTestCases(batchManager, executedTests);
		}

		assertThat(executedTestResults.get(0), hasItems(2));
		assertThat(executedTestResults.get(1), hasItems(3, 4));
		assertThat(executedTestResults.get(2), hasItems(1, 5, 6, 7));
	}

	@Test
	public void should_split_tests_between_batches_optimally_when_tests_are_run_in_parallel() throws InitializationError, InterruptedException {
		List<TestRunnerThread> threads = new ArrayList<TestRunnerThread>();
		
		for (int i = 1; i <= 3; i++) {
			MockEnvironmentVariables threadVariables = new MockEnvironmentVariables();
			threadVariables.setProperty("thucydides.batch.count", "3");
			threadVariables.setProperty("thucydides.batch.number", Integer.toString(i));
			TestCountBasedBatchManager batchManager = new TestCountBasedBatchManager(threadVariables);
			threads.add(new TestRunnerThread(batchManager));
		}
		
		for(TestRunnerThread thread : threads) {
            thread.start();
        }

        for(TestRunnerThread thread : threads) {
            thread.join();
        }

		assertThat(threads.get(0).getExecutedTests(), hasItems(2));
		assertThat(threads.get(1).getExecutedTests(), hasItems(3, 4));
		assertThat(threads.get(2).getExecutedTests(), hasItems(1, 5, 6, 7));
	}

	private void runPreparedTestCases(TestCountBasedBatchManager batchManager, List<Integer> executedTests) {
		for (int testNumber = 1; testNumber <= testCountInTestCase.size(); testNumber++) {
			batchManager.registerTestCase("Test Case " + counter.getAndIncrement());
			if (batchManager.shouldExecuteThisTest(testCountInTestCase.get(testNumber - 1))) {
				executedTests.add(testNumber);
			}
		}
	}

	class TestRunnerThread extends Thread {
		private final TestCountBasedBatchManager batchManager;
		private List<Integer> executedTests = new ArrayList<Integer>();

		public TestRunnerThread(final TestCountBasedBatchManager batchManager) throws InitializationError {
			this.batchManager = batchManager;
			executedTests = new ArrayList<Integer>();
		}

		public void run() {
			runPreparedTestCases(batchManager, executedTests);
		}

		public List<Integer> getExecutedTests() {
			return executedTests;
		}
	}
}
