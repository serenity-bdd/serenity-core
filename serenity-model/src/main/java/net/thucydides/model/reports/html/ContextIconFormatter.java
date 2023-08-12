package net.thucydides.model.reports.html;

import net.thucydides.model.domain.ContextIcon;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.requirements.reports.ScenarioOutcome;

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
