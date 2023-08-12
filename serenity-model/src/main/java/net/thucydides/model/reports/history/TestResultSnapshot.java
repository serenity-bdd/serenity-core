package net.thucydides.model.reports.history;

import org.joda.time.DateTime;

public class TestResultSnapshot implements Comparable<TestResultSnapshot> {

    private final DateTime time;
    private final int specifiedSteps;
    private final int passingSteps;
    private final int failingSteps;
    private final int skippedSteps;
    private final String buildId;

    public TestResultSnapshot(final DateTime time,
                              final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId) {
        this.time = time;
        this.specifiedSteps = specifiedSteps;
        this.passingSteps = passingSteps;
        this.failingSteps = failingSteps;
        this.skippedSteps = skippedSteps;
        this.buildId = buildId;
    }

    public TestResultSnapshot(final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId) {
        this(DateTime.now(),specifiedSteps,passingSteps,failingSteps,skippedSteps,buildId);
    }

    public DateTime getTime() {
        return time;
    }

    public int getSpecifiedSteps() {
        return specifiedSteps;
    }

    public int getPassingSteps() {
        return passingSteps;
    }

    public int getFailingSteps() {
        return failingSteps;
    }

    public int getSkippedSteps() {
        return skippedSteps;
    }

    public String getBuildId() {
        return buildId;
    }

    public int compareTo(TestResultSnapshot other) {
        if (this == other) {
            return 0;
        } else {
            return this.getTime().compareTo(other.getTime());
        }
    }
}
