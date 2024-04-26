package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.TestResult;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ExampleOutcome {
    private final String title;
    private final String subtitle;
    private final TestResult result;
    private ZonedDateTime startTime;
    private final long duration;
    private final long stepCount;
    private final String allStepsText;

    public ExampleOutcome(String title, String subtitle, TestResult result, ZonedDateTime startTime, long duration, long stepCount, String allStepsText) {
        this.title = title;
        this.subtitle = subtitle;
        this.result = result;
        this.startTime = startTime;
        this.duration = duration;
        this.stepCount = stepCount;
        this.allStepsText = allStepsText;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasSubtitle() {
        return (subtitle != null && !subtitle.isEmpty());
    }

    public String getSubtitle() {
        return subtitle;
    }

    public TestResult getResult() {
        return result;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getStepCount() {
        return stepCount;
    }

    public String getAllStepsText() {
        return allStepsText;
    }

    public String getFormattedStartTime() {
        return (startTime != null) ? "" + startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "";
    }

    public String getFormattedDuration() {
        return  (duration != 0L) ? "" + CompoundDuration.of(duration) : "";
    }

}
