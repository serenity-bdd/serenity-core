package net.serenitybdd.cucumber.suiteslicing;

import com.google.gson.GsonBuilder;
import net.serenitybdd.cucumber.util.PathUtils;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY;

public class CucumberScenarioVisualiser {

    private final Logger LOGGER = LoggerFactory.getLogger(CucumberScenarioVisualiser.class);
    private final EnvironmentVariables environmentVariables;

    public CucumberScenarioVisualiser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }


    private String outputDirectory() {
        return environmentVariables.getProperty(SERENITY_OUTPUT_DIRECTORY, "target/site/serenity");
    }

    public static List<VisualisableCucumberScenarios> sliceIntoForks(int forkCount, List<WeightedCucumberScenarios> slices) {
        return slices.stream()
            .map(slice -> IntStream.rangeClosed(1, forkCount).mapToObj(forkNumber -> VisualisableCucumberScenarios.create(slices.indexOf(slice) + 1, forkNumber, slice.slice(forkNumber).of(forkCount)))
                .collect(toList())).flatMap(List::stream).collect(toList());
    }

    public void visualise(URI rootFolderURI, int sliceCount, int forkCount, TestStatistics testStatistics) {
        try {
            Files.createDirectories(Paths.get(outputDirectory()));
            List<WeightedCucumberScenarios> slices = new CucumberScenarioLoader(newArrayList(rootFolderURI), testStatistics).load().sliceInto(sliceCount);
            List<VisualisableCucumberScenarios> visualisedSlices = CucumberScenarioVisualiser.sliceIntoForks(forkCount, slices);
            String jsonFile = String.format("%s/%s-slice-config-%s-forks-in-each-of-%s-slices-using-%s.json", outputDirectory(), PathUtils
                .getAsFile(rootFolderURI).getPath().replaceAll("[:/]", "-"), forkCount, sliceCount, testStatistics);
            Files.write(Paths.get(jsonFile), new GsonBuilder().setPrettyPrinting().create().toJson(visualisedSlices).getBytes());
            LOGGER.debug("Wrote visualisation as JSON for {} slices -> {}", visualisedSlices.size(), jsonFile);
        } catch (Exception e) {
            throw new RuntimeException("failed to visualise scenarios", e);
        }
    }

}