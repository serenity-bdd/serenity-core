package net.thucydides.core.requirements.model.cucumber;

public class ExampleRowResultIcon implements RowResultIcon {
    private final String fullScenarioName;

    public ExampleRowResultIcon(String featureName, String scenarioName) {
        this.fullScenarioName = featureName + "!" + scenarioName;
    }

    private static String RESULT_TOKEN_TEMPLATE = "{example-result:%s[%d]}";
    public String resultToken(int lineNumber) {
        return String.format(RESULT_TOKEN_TEMPLATE, fullScenarioName, lineNumber);
    }

    public SummaryRowResultIcon summaryIcon() {
        return new SummaryRowResultIcon(fullScenarioName);
    }

    public RowResultIcon noIcon() {
        return new BlankRowResultIcon();
    }
}