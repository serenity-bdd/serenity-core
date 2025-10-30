package net.thucydides.model.requirements;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestType;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.reports.RequirementOutcome;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenCountingRequirementsInOutcomes {

    File featuresDirectory;
    File outcomeDirectory;

    @Before
    public void findSourceDirectories() throws URISyntaxException {
        featuresDirectory = new File(ClassLoader.getSystemClassLoader().getResource("serenity-cucumber/features").getFile());
        outcomeDirectory =  new File(ClassLoader.getSystemClassLoader().getResource("serenity-cucumber/json").getFile());
    }

    @Test
    public void should_find_correct_total_requirement_outcome_count_for_cucumber_jvm_outcomes() throws URISyntaxException, IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        assertThat(outcomes.getOverview(), is("## Requirements Overview"));
    }

    @Test
    public void should_find_overview_text_for_cucumber_jvm_outcomes() throws URISyntaxException, IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        assertThat(outcomes.count(TestType.ANY).getTotal(), is(17));
        assertThat(outcomes.getScenarioCount(), is(11L));
        assertThat(outcomes.getTestCaseCount(), is(17L));
    }

    @Test
    public void should_find_correct_requirement_outcome_count_for_top_level_requirements_in_cucumber_jvm_outcomes() throws URISyntaxException, IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        for(Requirement topLevelRequirement : fileSystemRequirements.getRequirementsService().getRequirements()) {
            assertThat(outcomes.getTestOutcomes().forRequirement(topLevelRequirement).count(TestType.ANY).getTotal(), greaterThan(1) );
        }
    }


    @Test
    public void should_find_correct_requirements_coverage_for_top_level_requirements_in_cucumber_jvm_outcomes() throws URISyntaxException, IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        assertThat(outcomes.getProportion().withResult(TestResult.SUCCESS), equalTo(1.0));
    }

    @Test
    public void should_find_correct_requirement_outcome_count_for_requirements_without_tests_in_cucumber_jvm_outcomes() throws IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        for(RequirementOutcome requirementsOutcome : outcomes.getRequirementOutcomes()) {
            assertThat(requirementsOutcome.getRequirementsWithoutTestsCount(), equalTo(0L));
        }
    }

    @Test
    public void should_find_correct_requirement_outcome_count_for_top_level_requirements_in_cucumber_js_outcomes() throws IOException {

        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());

        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));

        for(RequirementOutcome requirementsOutcome : outcomes.getRequirementOutcomes()) {
            assertThat(requirementsOutcome.getRequirementsWithoutTestsCount(), equalTo(0L));
        }
    }
}
