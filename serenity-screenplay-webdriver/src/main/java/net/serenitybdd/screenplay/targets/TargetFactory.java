package net.serenitybdd.screenplay.targets;

public class TargetFactory {
    public static TargetBuilder the(String targetElementName) {
        return new TargetBuilder(targetElementName);
    }
}
