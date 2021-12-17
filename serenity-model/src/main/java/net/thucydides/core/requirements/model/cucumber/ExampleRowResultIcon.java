package net.thucydides.core.requirements.model.cucumber;

public class ExampleRowResultIcon implements RowResultIcon {
    private final String featureName;

    public ExampleRowResultIcon(String featureName) {
        this.featureName = featureName;
    }

    private static final String RESULT_TOKEN_TEMPLATE = "{example-result:%s[%d]}";

    public String resultToken(long lineNumber) {
        return String.format(RESULT_TOKEN_TEMPLATE, featureName, lineNumber);
    }

    public RowResultIcon noIcon() {
        return new BlankRowResultIcon();
    }
}