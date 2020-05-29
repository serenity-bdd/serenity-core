package net.thucydides.core.requirements.model.cucumber;

public class BlankRowResultIcon implements RowResultIcon {
    @Override
    public String resultToken(int lineNumber) {
        return "&nbsp;";
    }
}
