package net.serenitybdd.cucumber.suiteslicing;


import net.serenitybdd.cucumber.util.BigDecimalAverageCollector;
import net.thucydides.model.util.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class MultiRunTestStatistics implements TestStatistics {

    private final List<TestScenarioResults> results = newArrayList();

    static Logger LOGGER = LoggerFactory.getLogger(MultiRunTestStatistics.class);

    public static MultiRunTestStatistics fromRelativePath(String basePath) {

        MultiRunTestStatistics multiRunTestStatistics = new MultiRunTestStatistics();
        try {
            URI uri = MultiRunTestStatistics.class.getResource(basePath).toURI();
            Path resultsPath;
            LOGGER.info("Loading results from {}", uri);
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                } catch (FileSystemAlreadyExistsException e) {
                    fileSystem = FileSystems.getFileSystem(uri);
                }
                resultsPath = fileSystem.getPath(basePath);
            } else {
                resultsPath = Paths.get(uri);
            }

            Files.walk(resultsPath).filter(path -> Files.isRegularFile(path)).forEach(file -> {
                LOGGER.debug("Aggregating results from {}", file.getFileName());
                multiRunTestStatistics.addStatistics(SingleRunTestStatistics.fromFileName(basePath + "/" + file.getFileName()));
            });
        } catch (Exception e) {
            throw new RuntimeException(String.format("could not open scenario results from %s", basePath), e);
        }

        return multiRunTestStatistics;
    }

    @Override
    public BigDecimal scenarioWeightFor(String feature, String scenario) {
        return records().stream()
            .filter(record -> record.feature.equals(feature) && record.scenario.equals(scenario))
            .map(TestScenarioResult::duration)
            .findFirst()
            .orElseGet(() -> average(feature, scenario));
    }

    @Override
    public List<TestScenarioResult> records() {
        return results.stream().map(TestScenarioResults::average).collect(toList());
    }

    private void addStatistics(TestStatistics statistics) {
        statistics.records().forEach(record -> {
            Optional<TestScenarioResults> existingResult = results.stream().filter(existing -> existing.scenarioKey.equals(record.scenarioKey)).findFirst();
            if (existingResult.isPresent()) {
                existingResult.get().addDuration(record.duration);
            } else {
                results.add(TestScenarioResults.create(record));
            }
        });
    }

    private BigDecimal averageDuration() {
        return records().stream().map(TestScenarioResult::duration).collect(BigDecimalAverageCollector.create());
    }

    private BigDecimal average(String feature, String scenario) {
        LOGGER.warn("Returning average weighting of {} due to non-match of {} -> {}", averageDuration(), feature, scenario);
        return averageDuration();
    }

    public String toString() {
        return  Inflector.getInstance().kebabCase(this.getClass().getSimpleName());
    }

}
