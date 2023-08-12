package net.thucydides.model.reports.json;

public class AScenarioHasNoNameException extends RuntimeException {
    public AScenarioHasNoNameException(String id) {
        super("A scenario with no name was found for: " + id);
    }
}
