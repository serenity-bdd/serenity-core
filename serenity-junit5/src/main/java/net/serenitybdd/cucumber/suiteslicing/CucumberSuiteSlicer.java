package net.serenitybdd.cucumber.suiteslicing;

import io.cucumber.gherkin.CucumberScenarioLoader;
import net.serenitybdd.cucumber.utils.TagParser;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;

public class CucumberSuiteSlicer {

    private final List<URI> featurePaths;
    private final TestStatistics statistics;

    public CucumberSuiteSlicer(List<URI> featurePaths, TestStatistics statistics) {
        this.featurePaths = featurePaths;
        this.statistics = statistics;
    }

    public WeightedCucumberScenarios scenarios(int batchNumber, int batchCount, int forkNumber, int forkCount, List<String> tagFilters) {
        return new CucumberScenarioLoader(featurePaths, statistics).load()
            .filter(forSuppliedTags(tagFilters))
            .slice(batchNumber).of(batchCount).slice(forkNumber).of(forkCount);
    }

    private Predicate<WeightedCucumberScenario> forSuppliedTags(List<String> tagFilters) {
        return cucumberScenario -> TagParser.parseFromTagFilters(tagFilters).evaluate(newArrayList(cucumberScenario.tags));
    }
}
