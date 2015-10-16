package net.serenitybdd.screenplay.targets;

public class Target {
    private final String targetElementName;
    private final String cssOrXPathSelector;

    public Target(String targetElementName, String cssOrXPathSelector) {
        this.targetElementName = targetElementName;
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public String getTargetElementName() {
        return targetElementName;
    }

    public String getCssOrXPathSelector() {
        return cssOrXPathSelector;
    }

    @Override
    public String toString() {
        return targetElementName;
    }

    public static TargetBuilder the(String targetElementName) {
        return new TargetBuilder(targetElementName);
    }


}
