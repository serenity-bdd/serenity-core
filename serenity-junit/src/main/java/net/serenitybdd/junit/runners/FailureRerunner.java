package net.serenitybdd.junit.runners;


import java.util.List;
import java.util.Map;

public interface FailureRerunner {

    /**
     * Stores failed tests.
     *
     * @param failedTests map keys are class names, values lists with failed method names
     */
    public void recordFailedTests(Map<String, List<String>> failedTests);

    /**
     * Returns true if a test given by className and method name has to be run.
     * @param className
     * @param methodName
     * @return
     */
    public boolean hasToRunTest(String className, String methodName);
}
