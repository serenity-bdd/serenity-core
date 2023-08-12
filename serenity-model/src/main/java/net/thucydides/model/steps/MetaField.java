package net.thucydides.model.steps;

public class MetaField {

    private final String stepDescription;

    public MetaField(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public static MetaField from(String stepDescription) {
        return new MetaField(stepDescription);
    }

    public boolean isDefined() {
        return stepDescription.startsWith("!#");
    }

    public String template() {
        return stepDescription.substring(1);
    }

    public String fieldName()  {
        return stepDescription.substring(2);
    }
}
