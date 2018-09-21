package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Feature;

public class ScenarioReport {
    private final String scenarioName;

    private ScenarioReport(String scenarioName) {

        this.scenarioName = scenarioName;
    }

    public static ScenarioReport forScenario(String scenarioName) {
        return new ScenarioReport(scenarioName);
    }

    public String inFeature(Feature feature) {
        return convertToId(feature.getName()) + "_" + convertToId(scenarioName) + ".html";
    }


    static String convertToId(String name) {
        return name.replaceAll("[\\s'_,!]", "-").toLowerCase();
    }
}
