package net.thucydides.model.requirements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * This class tests the filtering of a "JSONRequirementsTree" based on tags.
 * <p>
 * The report filters the requirements tree based on the tags specified in the "cucumber.filter.tags" property.
 * Therefore, the filtering should work with tags which can be given in a "cucumber.filter.tags" property.
 * </p>
 */
public class WhenFilteringARequirementsTreeByTags {

    File featuresDirectory;
    File outcomeDirectory;

    @Before
    public void findSourceDirectories() {
        featuresDirectory = new File(ClassLoader.getSystemClassLoader().getResource("requirements-tree/features").getFile());
        outcomeDirectory = new File(ClassLoader.getSystemClassLoader().getResource("requirements-tree/json").getFile());
    }

    @Test
    public void should_filter_by_scenario_tag_on_all_scenarios() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "@scenario_add_item");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(1));
    }

    @Test
    public void should_filter_by_scenario_tag_on_one_scenario() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "@scenario_milk");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(1));
    }

    @Test
    public void should_filter_by_scenario_tag_with_colon_char_separator() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "@scenario_colon:milk");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(1));
    }

    @Test
    public void should_filter_by_scenario_tag_with_equal_char_separator() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "@scenario_equ=milk");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(1));
    }

    @Test
    public void should_omit_all_for_not_existing_tag() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "@not_existing_tag");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(0));
    }

    @Test
    public void should_list_all_for_empty_tag() throws IOException {
        FileSystemRequirements fileSystemRequirements = new FileSystemRequirements(featuresDirectory.getPath());
        RequirementsOutcomes outcomes = fileSystemRequirements.getRequirementsOutcomeFactory().buildRequirementsOutcomesFrom(TestOutcomeLoader.testOutcomesIn(outcomeDirectory));
        List<Requirement> requirements = outcomes.getRequirements();

        JSONRequirementsTree tree = JSONRequirementsTree.forRequirements(requirements, outcomes, "");
        JsonNode jsonTree = new ObjectMapper().readValue(tree.asString(), JsonNode.class);
        assertThat(jsonTree.size(), is(1));
    }

}
