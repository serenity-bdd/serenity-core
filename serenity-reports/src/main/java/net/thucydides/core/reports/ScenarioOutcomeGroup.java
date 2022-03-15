package net.thucydides.core.reports;

import net.thucydides.core.model.Rule;
import net.thucydides.core.model.RuleBackground;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.html.TagFilter;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.*;
import java.util.stream.Collectors;

public class ScenarioOutcomeGroup {

    private String ruleName = "";
    private String ruleDescription = "";
    private String backgroundTitle = "";
    private String backgroundDescription = "";
    private String id;
    private RuleBackground background;
    private final List<ScenarioOutcome> scenarios;
    private final List<TestTag> tags;

    public ScenarioOutcomeGroup(Rule rule, List<ScenarioOutcome> scenarios) {
        this(scenarios);
        this.ruleName = assureNotNull(rule.getName());
        this.ruleDescription = assureNotNull(rule.getDescription());
        if (rule.hasBackground()) {
            this.background = rule.getBackground();
            this.backgroundTitle = rule.getBackground().getName();
            this.backgroundDescription = rule.getBackground().getDescription();
        }
        if (rule.getTags() != null) {
            this.tags.addAll(rule.getTags());
        }
        this.id = UUID.randomUUID().toString();
    }

    private RuleBackground backgroundFrom(List<ScenarioOutcome> scenarios) {
        return scenarios.stream()
                .filter(scenarioOutcome -> scenarioOutcome.getType().equalsIgnoreCase("background"))
                .findFirst()
                .map(
                        scenarioOutcome -> new RuleBackground("Overall background",
                                "Overall background description",
                                scenarioOutcome.getSteps())
                ).get();
    }

    public ScenarioOutcomeGroup(List<ScenarioOutcome> scenarios) {
        this.scenarios = scenarios;
        this.id = UUID.randomUUID().toString();
        if (backgroundScenariosIn(scenarios)) {
            this.background = backgroundFrom(scenarios);
        }
        this.tags = new ArrayList<>();
    }

    private boolean backgroundScenariosIn(List<ScenarioOutcome> scenarios) {
        return scenarios.stream().anyMatch(scenarioOutcome -> scenarioOutcome.getType().equalsIgnoreCase("background"));
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public boolean hasBackground() {
        return background != null;
    }

    public RuleBackground getBackground() {
        return background;
    }

    public List<String> getBackgroundSteps() {
        if (hasBackground()) {
            return background.getSteps();
        } else if (hasBackgroundScenario()) {
            return getBackgroundScenario().getSteps();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean hasBackgroundScenario() {
        return scenarios.stream().anyMatch(ScenarioOutcome::isBackground);
    }

    public ScenarioOutcome getBackgroundScenario() {
        return scenarios.stream().filter(ScenarioOutcome::isBackground).findFirst().get();
    }

    public List<ScenarioOutcome> getScenarios() {
        return scenarios;
    }

    public boolean hasScenarios() {
        return !scenarios.isEmpty();
    }

    public List<ScenarioOutcome> getMainScenarios() {
        return scenarios.stream()
                .filter(scenarioOutcome -> !scenarioOutcome.isBackground())
                .filter(scenarioOutcome -> !scenarioOutcome.getName().isEmpty())
                .collect(Collectors.toList());
    }

    private String assureNotNull(String anyString) {
        if (anyString == null || anyString.isEmpty()) {
            return "";
        }
        return anyString;
    }

    public String getId() {
        return id;
    }

    public String getBackgroundTitle() { return backgroundTitle; }
    public String getBackgroundDescription() { return backgroundDescription; }

    public void setBackgroundTitle(String backgroundTitle) {
        this.backgroundTitle = backgroundTitle;
    }

    public void setBackgroundDescription(String backgroundDescription) {
        this.backgroundDescription = backgroundDescription;
    }

    public List<TestTag> getTags() {
        return tags;
    }

    public Set<TestTag> getFilteredTags() {
        TagFilter tagFilter = new TagFilter();
        Set<TestTag> filteredTags = new HashSet<>(tags);
        return tagFilter.removeHiddenTagsFrom(filteredTags);
    }
}

