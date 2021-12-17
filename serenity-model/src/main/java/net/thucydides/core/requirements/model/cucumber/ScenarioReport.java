package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;

public class ScenarioReport {
    private final String scenarioName;

    private ScenarioReport(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public static ScenarioReport forScenario(String scenarioName) {
        return new ScenarioReport(scenarioName);
    }

    public String inFeature(Feature feature) {
        return ReportNamer.forReportType(ReportType.HTML)
                          .getNormalizedTestReportNameFor(convertToId(feature.getName()) + "_" + convertToId(scenarioName));
    }

    static String convertToId(String name) {
        return name.replaceAll("[\\s'!,]", "-").toLowerCase();
    }
}
