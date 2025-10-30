package net.serenitybdd.cucumber.util;

import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TagParserFromEnvironmentVariablesTest {

    @Test
    public void commandLineTagsNotInRunTimeTags() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("tags", "@my_tag_from_command_line, @my_health_coc,@another_tag_from_command_line");
        assertThat(TagParser.additionalTagsSuppliedFrom(environmentVariables, asList("@smoke", "@my_health_coc", "@ManageFeatureToggles")),
                   containsInAnyOrder("@my_tag_from_command_line", "@another_tag_from_command_line"));
    }

    @Test
    public void commandLineTagsAllInRunTimeTags() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("tags", "@my_health_coc");
        assertThat(TagParser.additionalTagsSuppliedFrom(environmentVariables, asList("@smoke", "@my_health_coc", "@ManageFeatureToggles")),
                   hasSize(0));
    }

    @Test
    public void noCommandLineTagsProvided() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(TagParser.additionalTagsSuppliedFrom(environmentVariables, asList("@smoke", "@my_health_coc", "@ManageFeatureToggles")),
                   hasSize(0));
    }
}
