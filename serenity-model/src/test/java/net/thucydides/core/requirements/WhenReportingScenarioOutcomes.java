package net.thucydides.core.requirements;

import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcomes;
import net.thucydides.core.requirements.reports.cucumber.FeatureCache;
import net.thucydides.core.requirements.reports.cucumber.FeatureFileScenarioOutcomes;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
