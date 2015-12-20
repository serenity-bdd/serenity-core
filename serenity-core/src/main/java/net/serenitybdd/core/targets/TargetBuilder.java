package net.serenitybdd.core.targets;

public class TargetBuilder<T> {
    private String targetElementName;

    public TargetBuilder(String targetElementName) {
        this.targetElementName = targetElementName;
    }

    public Target locatedBy(String cssOrXPathSelector) {
        return new Target(targetElementName, cssOrXPathSelector);
    }

    public TargetBuilder<T> onElementNamed(String targetElementName) {
        this.targetElementName = targetElementName;
        return this;
    }
}
