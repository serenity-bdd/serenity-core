package net.thucydides.model.domain;

import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Enclosed.class)
public class TestResultTest {

    @RunWith(Parameterized.class)
    public static class IsMoreSevereTest {
        private final TestResult firstStatus;
        private final TestResult secondStatus;
        private final boolean expectedResult;

        public IsMoreSevereTest(TestResult firstStatus, TestResult secondStatus, boolean expectedResult) {
            this.firstStatus = firstStatus;
            this.secondStatus = secondStatus;
            this.expectedResult = expectedResult;
        }

        @Test
        public void should_detect_more_severe_results() {
            Assertions.assertEquals(firstStatus.isMoreSevereThan(secondStatus), expectedResult);
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {TestResult.FAILURE, TestResult.SUCCESS, true},
                    {TestResult.FAILURE, TestResult.FAILURE, false},
                    {TestResult.SUCCESS, TestResult.FAILURE, false}
            });
        }
    }

    @RunWith(Parameterized.class)
    public static class IsLessSevereTest {
        private final TestResult firstStatus;
        private final TestResult secondStatus;
        private final boolean expectedResult;

        public IsLessSevereTest(TestResult firstStatus, TestResult secondStatus, boolean expectedResult) {
            this.firstStatus = firstStatus;
            this.secondStatus = secondStatus;
            this.expectedResult = expectedResult;
        }

        @Test
        public void should_detect_less_severe_results() {
            Assertions.assertEquals(firstStatus.isLessSevereThan(secondStatus), expectedResult);
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {TestResult.FAILURE, TestResult.SUCCESS, false},
                    {TestResult.FAILURE, TestResult.FAILURE, false},
                    {TestResult.SUCCESS, TestResult.FAILURE, true}
            });
        }
    }
}
