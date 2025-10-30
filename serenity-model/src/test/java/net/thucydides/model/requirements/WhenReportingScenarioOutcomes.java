package net.thucydides.model.requirements;

import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import net.thucydides.model.requirements.reports.ScenarioOutcomes;
import net.thucydides.model.requirements.reports.cucumber.FeatureCache;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WhenReportingScenarioOutcomes {

    File featuresDirectory;
    File outcomeDirectory;

    @Before
    public void findSourceDirectories() {
        FeatureCache.getCache().close();
        featuresDirectory = new File(ClassLoader.getSystemClassLoader().getResource("serenity-cucumber/features").getFile());
        outcomeDirectory =  new File(ClassLoader.getSystemClassLoader().getResource("serenity-cucumber/json").getFile());
    }

    @Test
    public void should_extract_scenarios_from_capability_results() throws IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        assertThat(ScenarioOutcomes.from(outcomes).size(), equalTo(11));
    }

}
