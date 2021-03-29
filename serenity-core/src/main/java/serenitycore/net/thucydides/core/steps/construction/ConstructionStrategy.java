package serenitycore.net.thucydides.core.steps.construction;

public enum ConstructionStrategy {
    DEFAULT_CONSTRUCTOR,
    STEP_LIBRARY_WITH_WEBDRIVER,
    STEP_LIBRARY_WITH_PAGES,
    CONSTRUCTOR_WITH_PARAMETERS,
    INNER_CLASS_CONSTRUCTOR
}