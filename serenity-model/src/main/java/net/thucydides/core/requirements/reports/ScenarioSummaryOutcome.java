package net.thucydides.core.requirements.reports;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScenarioSummaryOutcome implements ScenarioOutcome {
    private final String name;
    private final String type;
    private final String id;
    private final TestResult result;
    private final List<String> reportBadges;
    private final String scenarioReport;
    private final String description;
    private final List<String> steps;
    private final List<String> examples;
    private final int exampleCount;
    private final String parentName;
    private final String parentReport;
    private final Boolean manual;
    private final Set<TestTag> tags;
    private final Map<String, Collection<TestTag>> exampleTags;

    public ScenarioSummaryOutcome(String name,
                                  String type,
                                  TestResult result,
                                  List<String> reportBadges,
                                  String scenarioReport,
                                  String description,
                                  List<String> steps,
                                  List<String> examples,
                                  int exampleCount,
                                  Boolean isManual,
                                  String parentName,
                                  String parentReport,
                                  Set<TestTag> tags,
                                  Map<String, Collection<TestTag>> exampleTags) {
        this.name = name;
        this.type = type;
        this.id = Digest.ofTextValue(name);
        this.result = result;
        this.reportBadges = reportBadges;
        this.scenarioReport = scenarioReport;
        this.description = description;
        this.steps = steps;
        this.examples = examples;
        this.exampleCount = exampleCount;
        this.parentName = parentName;
        this.parentReport = parentReport;
        this.manual = isManual;
        this.tags = tags;
        this.exampleTags = exampleTags;
    }

    public String toString() {
        return "ScenarioSummaryOutcome for " + name;
    }


    public String getName() {
        return name;
    }

    public String getSimplifiedName() { return name; }

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

    public String getType() {
        return type;
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

    public boolean hasExamples() { return exampleCount > 0; }

    public String getNumberOfExamples() { return (exampleCount == 1) ? "1 example" : exampleCount + " examples"; }


    public String getScenarioReport() {
        return scenarioReport;
    }

    public List<String> getScenarioReportBadges() {
        return reportBadges;
    }

    public Integer getStepCount() { return steps.size(); }


    public ZonedDateTime getStartTime() {
        return null;
    }

    public Long getDuration() {
        return 0L;
    }

    public Boolean isManual() {
        return manual;
    }

    public String getFormattedStartTime() {
        return " ";
    }

    public String getFormattedDuration() {
        return " ";
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentReport() {
        return parentReport;
    }

    @Override
    public Set<TestTag> getTags() {
        return tags;
    }

    @Override
    public Map<String, Collection<TestTag>> getExampleTags() {
        return exampleTags;
    }
}
