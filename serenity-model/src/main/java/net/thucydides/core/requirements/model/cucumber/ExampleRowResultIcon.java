package net.thucydides.core.requirements.model.cucumber;

public class ExampleRowResultIcon implements RowResultIcon {
    private int exampleRow = 0;
    private final String fullScenarioName;

    public ExampleRowResultIcon(String fullScenarioName) {
        this.fullScenarioName = fullScenarioName;
    }

    public String resultToken() {
        return "{example-result:" + fullScenarioName + "[" + exampleRow++ + "]}";
    }

    public SummaryRowResultIcon summaryIcon() {
        return new SummaryRowResultIcon(fullScenarioName);
    }
}