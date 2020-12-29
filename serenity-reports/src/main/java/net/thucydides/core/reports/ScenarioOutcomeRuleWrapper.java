package net.thucydides.core.reports;

import net.thucydides.core.requirements.reports.ScenarioOutcome;

import java.util.List;

public class ScenarioOutcomeRuleWrapper {

    private String rule;
    private final List<ScenarioOutcome> scenarios;

    public ScenarioOutcomeRuleWrapper(String newRule, List<ScenarioOutcome> scenarios) {
        if(newRule != null && ScenarioOutcome.RULE_NOT_SET.equals(newRule)) {
            this.rule = "";
        } else {
            this.rule = newRule;
        }
        this.scenarios = scenarios;
    }

    public String getRule() {
        return rule;
    }
    public List<ScenarioOutcome> getScenarios() {
        return scenarios;
    }

}
