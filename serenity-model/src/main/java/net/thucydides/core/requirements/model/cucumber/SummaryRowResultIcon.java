package net.thucydides.core.requirements.model.cucumber;

public class SummaryRowResultIcon implements RowResultIcon {

    private final String fullScenarioName;

    public SummaryRowResultIcon(String fullScenarioName) {
        this.fullScenarioName = fullScenarioName;
    }

    public String resultToken(int lineNumber) {
        return "**{result:" + fullScenarioName  + "}**";
    }
}