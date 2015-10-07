package net.serenitybdd.screenplay.targets;

/**
 * Created by john on 22/08/2015.
 */
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
