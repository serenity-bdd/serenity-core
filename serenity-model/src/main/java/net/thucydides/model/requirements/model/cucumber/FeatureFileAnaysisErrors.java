package net.thucydides.model.requirements.model.cucumber;

public class FeatureFileAnaysisErrors {
    public static final String EMPTY_FEATURE_NAME
            = "The feature name in '%s' is empty" + System.lineSeparator()
              + "    Each feature file should have a name, e.g. Feature: Adding new items to the cart";

    public static final String EMPTY_SCENARIO_NAME
            = "Empty scenario names were found in file '%s'" + System.lineSeparator()
            + "    Each scenario should have a unique name, e.g. Scenario: Adding a single item to the cart";

    public static final String EMPTY_RULE_NAME
            = "Empty rule names were found in file '%s'" + System.lineSeparator()
            + "    Each rule should have a unique name, e.g. Rule: Discount codes should appear in the shopping cart";

    public static final String DUPLICATE_SCENARIO_NAME
            = "The scenario name '%s' was duplicated in file '%s'" + System.lineSeparator()
            + "    Each scenario name in a feature file should be unique";

    public static final String DUPLICATE_FEATURE_NAME
            = "The feature/parent combination '%s' was found in several places in the feature directory structure" + System.lineSeparator()
            + "    Features are allowed to have duplicated names (although this can be confusing and is not recommended)" + System.lineSeparator()
            + "    But for the Serenity reports to work correctly, they must not be in directories with identical names" + System.lineSeparator()
            + "    For example: a/c/my-feature.feature and b/d/my-feature.feature is allowed (but not recommended)," + System.lineSeparator()
            + "    But a/c/my-feature.feature and b/c/my-feature.feature is not allowed" + System.lineSeparator()
            + "    Duplicate paths were found in the following files: " + System.lineSeparator()
            + "%s";

}
