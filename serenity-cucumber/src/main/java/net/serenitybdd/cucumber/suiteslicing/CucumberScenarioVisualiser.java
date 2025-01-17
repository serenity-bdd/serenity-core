package net.serenitybdd.cucumber.suiteslicing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cucumber.gherkin.CucumberScenarioLoader;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.cucumber.util.PathUtils;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class CucumberScenarioVisualiser {

    private final Logger LOGGER = LoggerFactory.getLogger(CucumberScenarioVisualiser.class);
    private final EnvironmentVariables environmentVariables;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CucumberScenarioVisualiser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }


    private String outputDirectory() {
        return ConfiguredEnvironment.getConfiguration().getOutputDirectory().getPath();
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
            Files.write(Paths.get(jsonFile),gson.toJson(visualisedSlices).getBytes());
            LOGGER.debug("Wrote visualisation as JSON for {} slices -> {}", visualisedSlices.size(), jsonFile);
        } catch (Exception e) {
            throw new RuntimeException("failed to visualise scenarios", e);
        }
    }

}
