package net.serenitybdd.cli.reporters;

import net.thucydides.model.requirements.AggregateRequirements;
import net.thucydides.model.requirements.Requirements;

import java.nio.file.Path;

/**
 * Created by john on 25/06/2016.
 */
public class RequirementsStrategy {
    public static RequirementsStrategyBuilder forJSONOutputsIn(Path jsonSourceDirectory) {
        return new RequirementsStrategyBuilder(jsonSourceDirectory);
    }

    public static class RequirementsStrategyBuilder {
        private final Path jsonSourceDirectory;

        public RequirementsStrategyBuilder(Path jsonSourceDirectory) {
            this.jsonSourceDirectory = jsonSourceDirectory;
        }

        public Requirements andFeatureFilesIn(String requirementsDirectory) {
            return new AggregateRequirements(jsonSourceDirectory, requirementsDirectory);
        }
    }
}
