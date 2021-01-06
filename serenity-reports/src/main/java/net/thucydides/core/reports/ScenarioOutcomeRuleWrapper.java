package net.thucydides.core.reports;

import net.thucydides.core.model.Rule;
import net.thucydides.core.requirements.reports.ScenarioOutcome;

import java.util.List;

public class ScenarioOutcomeRuleWrapper {

    private String ruleName = "";
    private String ruleDescription = "";
    private final List<ScenarioOutcome> scenarios;

    public ScenarioOutcomeRuleWrapper(Rule newRule, List<ScenarioOutcome> scenarios) {
        if(newRule != null ) {
            this.ruleName = assureNotNull(newRule.getName());
            this.ruleDescription = assureNotNull(newRule.getDescription());
        }
        this.scenarios = scenarios;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public List<ScenarioOutcome> getScenarios()
    {
        return scenarios;
    }

    private String assureNotNull(String anyString) {
        if(anyString == null || anyString.isEmpty()) {
            return "";
        }
        return anyString;
    }
}
