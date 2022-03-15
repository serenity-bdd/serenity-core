package net.thucydides.core.requirements.reports;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.Rule;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.EMPTY_MAP;

public class SingleScenarioOutcome implements ScenarioOutcome {
    private final String name;
    private final String simplifiedName;
    private final String type;
    private final String id;
    private final TestResult result;
    private final String scenarioReport;
    private final String description;
    private final List<String> steps;
    private final List<String> examples;
    private final int exampleCount;
    private final ZonedDateTime startTime;
    private final Long duration;
    private final Boolean manual;
    private final String parentName;
    private final String parentReport;
    private final Set<TestTag> tags;
    private Rule rule;
    private ExternalLink externalLink;
    private final Collection<TestTag> scenarioTags;

    public SingleScenarioOutcome(String name,
                                 String simplifiedName,
                                 String type,
                                 TestResult result,
                                 String scenarioReport,
                                 ZonedDateTime startTime,
                                 Long duration,
                                 Boolean manual,
                                 String description,
                                 List<String> steps,
                                 List<String> examples,
                                 int exampleCount,
                                 String parentName,
                                 String parentReport,
                                 Set<TestTag> tags,
                                 Rule rule,
                                 ExternalLink externalLink,
                                 Collection<TestTag> scenarioTags) {
        this.name = name;
        this.simplifiedName = simplifiedName;
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
        this.exampleCount = exampleCount;
        this.parentName = parentName;
        this.parentReport = parentReport;
        this.tags = tags;
        this.rule = rule;
        this.externalLink = externalLink;
        this.scenarioTags = scenarioTags;
    }

    public String toString() {
        return "SingleScenarioOutcome[" + name + "]";
    }

    public String getName() {
        return name;
    }

    public String getSimplifiedName() { return simplifiedName; }

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


    @Override
    public String getScenarioReport() {
        return scenarioReport;
    }

    @Override
    public List<String> getScenarioReportBadges() {
        return ReportBadges.forReport(scenarioReport);
    }

    public Integer getStepCount() { return steps.size(); }


    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public Boolean isManual() {
        return manual;
    }

    public Boolean isManualScenario() {
        return manual;
    }

    public String getFormattedStartTime() {
        return (startTime != null) ? "" + startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "";
    }

    public String getFormattedDuration() {
        return  (duration != 0L) ? "" + CompoundDuration.of(duration) : "";
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
        return EMPTY_MAP;
    }

    public Rule getRule() { return rule;}

    @Override
    public ExternalLink getExternalLink() {
        return externalLink;
    }

    public Collection<TestTag> getScenarioTags() {
        return scenarioTags;
    }
}
