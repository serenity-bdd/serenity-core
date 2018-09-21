package net.thucydides.core.requirements.reports;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.TestResult;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ScenarioOutcome {
    private final String name;
    private final String type;
    private final String id;
    private final TestResult result;
    private final String scenarioReport;
    private final String description;
    private final List<String> steps;
    private final List<String> examples;
    private final ZonedDateTime startTime;
    private final Long duration;
    private final boolean manual;

    public ScenarioOutcome(String name, String type, TestResult result,
                           String scenarioReport, ZonedDateTime startTime, Long duration) {
        this.name = name;
        this.type = type;
        this.id = Digest.ofTextValue(name);
        this.result = result;
        this.scenarioReport = scenarioReport;
        this.startTime = startTime;
        this.duration = duration;
        this.description = "";
        this.steps = Collections.EMPTY_LIST;
        this.examples = Collections.EMPTY_LIST;
        this.manual = false;
    }

    public ScenarioOutcome(String name,
                           String type,
                           TestResult result,
                           String scenarioReport,
                           ZonedDateTime startTime,
                           Long duration,
                           Boolean manual,
                           String description,
                           List<String> steps,
                           List<String> examples) {
        this.name = name;
        this.type = type;
        this.id = Digest.ofTextValue(name);
        this.result = result;
        this.scenarioReport = scenarioReport;
        this.startTime = startTime;
        this.duration = duration;
        this.manual = manual;
        this.description = description;
        this.steps = steps;
        this.examples = examples;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {

        if ("Background".equalsIgnoreCase(type)) {
            return backgroundTitle();
        }
        return name;
    }

    private String backgroundTitle() {
        if (name.isEmpty()) { return "Background"; }
        return "Background: " + name;
    }

    public String getId() {
        return id;
    }

    public TestResult getResult() {
        return result;
    }

    public String getResultStyle() {
        return result.name().toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<String> getExamples() {
        return examples;
    }

    public String getScenarioReport() {
        return scenarioReport;
    }

    public Integer getStepCount() { return steps.size(); }


    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public boolean isManual() {
        return manual;
    }


    public String getFormattedStartTime() {
        return (startTime != null) ? startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : " ";
    }

    public String getFormattedDuration() {
        return (duration != 0L) ? DurationFormatUtils.formatDuration(duration,"mm:ss") : " ";
    }
}
