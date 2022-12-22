package net.thucydides.core.reports.html;

import net.thucydides.core.model.ContextIcon;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcome;

public class ContextIconFormatter {
    public String forOutcome(ScenarioOutcome scenarioOutcome) {
        return ContextIcon.forOutcome(scenarioOutcome);
    }
    public String labelForOutcome(ScenarioOutcome scenarioOutcome) {
        return ContextIcon.labelForOutcome(scenarioOutcome);
    }

    public String forOutcome(TestOutcome outcome) {
        return ContextIcon.forOutcome(outcome);
    }


}
