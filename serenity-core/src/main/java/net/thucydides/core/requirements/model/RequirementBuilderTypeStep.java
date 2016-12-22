package net.thucydides.core.requirements.model;

public class RequirementBuilderTypeStep {
    final RequirementBuilderNameStep requirementBuilderNameStep;
    final String type;

    public RequirementBuilderTypeStep(RequirementBuilderNameStep requirementBuilderNameStep, String type) {
        this.requirementBuilderNameStep = requirementBuilderNameStep;
        this.type = type;
    }

    public Requirement withNarrative(String narrativeText) {
        String name = requirementBuilderNameStep.name;
        String displayName = requirementBuilderNameStep.displayName;
        String cardNumber = requirementBuilderNameStep.cardNumber;
        String parent = requirementBuilderNameStep.parent;
        String id = requirementBuilderNameStep.id;
        return new Requirement(name, id, displayName, cardNumber, parent, type, new CustomFieldValue("Narrative", narrativeText));
    }
}