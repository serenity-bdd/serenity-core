package net.serenitybdd.cucumber.suiteslicing;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ScenarioFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioFilter.class);
    private List<String> scenarios;
    private List<String> scenariosIncluded = newArrayList();
    private List<String> scenariosExcluded = newArrayList();

    private ScenarioFilter(List<String> scenarios) {
        this.scenarios = scenarios;
    }

    public static ScenarioFilter onScenarios(List<String> scenarios) {
        return new ScenarioFilter(scenarios);
    }

    @Override
    public String describe() {
        return String.format("Filters out all test steps except those in the list of scenarios: %s", scenarios);
    }

    @Override
    public boolean shouldRun(Description description) {
        String displayName = description.getDisplayName();
        String methodName = description.getMethodName().replaceAll(" #\\d+$",""); //For scenario outline method name contains #X where X is row number
        boolean shouldRun = scenarios.stream().anyMatch(methodName::equals) || displayName.startsWith("Examples") || displayName.contains("|");
        LOGGER.debug("Test should run: {} step: {}", shouldRun, description.getDisplayName());
        if (shouldRun) {
            scenariosIncluded.add(displayName);
        } else {
            scenariosExcluded.add(displayName);
        }
        return shouldRun;
    }

    public List<String> scenariosExcluded() {
        return scenariosExcluded;
    }

    public List<String> scenariosIncluded() {
        return scenariosIncluded;
    }
}
