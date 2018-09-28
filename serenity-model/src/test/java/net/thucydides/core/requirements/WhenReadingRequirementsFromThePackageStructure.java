package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WhenReadingRequirementsFromThePackageStructure {

    @Test
    public void should_read_requirements_from_a_one_level_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "packagerequirements");
        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("capability"));
    }

    @Test
    public void should_read_requirements_from_a_two_level_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "twolevelpackagerequirements");
        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("capability"));
        assertThat(requirements.get(0).getChildren().get(0).getType(), equalTo("feature"));
    }


    @Test
    public void should_read_requirements_from_an_uneven_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "unevenpackagerequirements");
        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("capability"));
        assertThat(requirements.get(0).getChildren().get(0).getType(), equalTo("feature"));
        assertThat(requirements.get(0).getChildren().get(0).getChildren().get(0).getType(), equalTo("feature"));
    }

}
