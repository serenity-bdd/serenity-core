package net.thucydides.core.requirements.model.cucumber;

public class ExampleRowResultIcon implements RowResultIcon {
//    private final String fullScenarioName;
    private final String featureName;

    public ExampleRowResultIcon(String featureName) {
//        this.fullScenarioName = featureName + "!" + scenarioName;
        this.featureName = featureName;
    }

    private static String RESULT_TOKEN_TEMPLATE = "{example-result:%s[%d]}";
    public String resultToken(int lineNumber) {
//        return String.format(RESULT_TOKEN_TEMPLATE, fullScenarioName, lineNumber);
        return String.format(RESULT_TOKEN_TEMPLATE, featureName, lineNumber);
    }

//    public SummaryRowResultIcon summaryIcon() {
//        return new SummaryRowResultIcon(featureName);
//        return new SummaryRowResultIcon(fullScenarioName);
//    }

    public RowResultIcon noIcon() {
        return new BlankRowResultIcon();
    }
}