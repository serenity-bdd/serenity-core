package serenitymodel.net.thucydides.core.model;

/**
 * A list of test results, used to determine the overall test result.
 */
public class TestResultComparison {

    private final TestResult testResultA;
    private final TestResult testResultB;

    public TestResultComparison(TestResult testResultA, TestResult testResultB) {

        this.testResultA = testResultA;
        this.testResultB = testResultB;
    }

    public static TestResult overallResultFor(TestResult testResultA, TestResult testResultB) {
        if (testResultA == TestResult.COMPROMISED || testResultB == TestResult.COMPROMISED) {
            return TestResult.COMPROMISED;
        }

        if (testResultA == TestResult.ERROR || testResultB == TestResult.ERROR) {
            return TestResult.ERROR;
        }

        if (testResultA == TestResult.FAILURE || testResultB == TestResult.FAILURE) {
            return TestResult.FAILURE;
        }

        if (testResultA == TestResult.PENDING || testResultB == TestResult.PENDING) {
            return TestResult.PENDING;
        }

        if (containsOnly(testResultA, testResultB, TestResult.IGNORED)) {
            return TestResult.IGNORED;
        }

        if (containsOnly(testResultA, testResultB, TestResult.SKIPPED)) {
            return TestResult.SKIPPED;
        }

        if (containsOnly(TestResult.SUCCESS, TestResult.IGNORED, TestResult.SKIPPED)) {
            return TestResult.SUCCESS;
        }
        return TestResult.SUCCESS;
    }

    private static boolean containsOnly(TestResult testResultA, TestResult testResultB, final TestResult... expectedValues) {
        for (TestResult expectedValue : expectedValues) {
            if ((testResultA != expectedValue) || (testResultB != expectedValue)) {
                return false;
            }
        }
        return true;
    }

}
