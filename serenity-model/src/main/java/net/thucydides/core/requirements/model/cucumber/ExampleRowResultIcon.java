package net.thucydides.core.requirements.model.cucumber;

public class ExampleRowResultIcon implements RowResultIcon {
    private int exampleTableNumber = 0;
    private int exampleRow = 0;
    private final String fullScenarioName;

    public ExampleRowResultIcon(String fullScenarioName, int exampleTableNumber) {
        this.fullScenarioName = fullScenarioName;
        this.exampleTableNumber = exampleTableNumber;
    }

    public String resultToken() {
        return "{example-result:" + fullScenarioName + "[" + exampleTableNumber + "][" + exampleRow++ + "]}";
    }

    public ExampleRowResultIcon nextExampleTable() {
        exampleTableNumber++;
        exampleRow = 0;
        return this;
    }

    public SummaryRowResultIcon summaryIcon() {
        return new SummaryRowResultIcon(fullScenarioName);
    }
}