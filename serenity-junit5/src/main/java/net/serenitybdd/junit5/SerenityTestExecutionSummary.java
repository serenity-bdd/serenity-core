package net.serenitybdd.junit5;

import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.concurrent.atomic.AtomicInteger;

public class SerenityTestExecutionSummary {
    public AtomicInteger containersSucceeded = new AtomicInteger(0);
    public AtomicInteger testsSucceeded = new AtomicInteger(0) ;
    public AtomicInteger containersAborted = new AtomicInteger(0);
    public AtomicInteger testsAborted = new AtomicInteger(0);
    public AtomicInteger containersFailed = new AtomicInteger(0);
    public AtomicInteger testsFailed = new AtomicInteger(0);

    public SerenityTestExecutionSummary(TestPlan testPlan) {
    }

    public void addFailure(TestIdentifier testIdentifier, Throwable throwable) {
    }
}
