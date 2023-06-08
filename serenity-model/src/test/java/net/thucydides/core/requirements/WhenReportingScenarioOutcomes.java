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

    private static final String RULE_1 = "Only completed items must be seen when the list is accordingly filtered";
    private static final String DESCRIPTION_1 = "Here follows the rule description...";
    private static final String RULE_2 = "Only completed items must be seen when the list is accordingly filtered with embedded tables";

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

        assertThat(ScenarioOutcomes.from(outcomes).size(), equalTo(5));
    }


    @Test
    public void should_load_feature_file_details_from_the_file_system() throws IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        Requirement requirement = outcomes.getRequirementOutcomes().get(0).getRequirement().getChildren().get(0);
        List<ScenarioOutcome> scenarioOutcomes = FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(outcomes);
        assertThat(scenarioOutcomes.size(), equalTo(4));

    }

    @Test
    public void should_load_scenario_rules() throws IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        Requirement requirement = outcomes.getRequirementOutcomes().get(0).getRequirement().getChildren().get(0);
        List<ScenarioOutcome> scenarioOutcomes = FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(outcomes);

        assertThat(scenarioOutcomes, hasSize(4));
        assertThat(scenarioOutcomes.get(0).getRule(),equalTo(null));
        assertThat(scenarioOutcomes.get(1).getRule().getName(),equalTo(RULE_1));
        assertThat(scenarioOutcomes.get(1).getRule().getDescription().trim(),equalTo(DESCRIPTION_1));
        assertThat(scenarioOutcomes.get(2).getRule().getName(),equalTo(RULE_1));
        assertThat(scenarioOutcomes.get(3).getRule().getName(),equalTo(RULE_2));
    }

}
