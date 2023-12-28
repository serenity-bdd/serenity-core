package net.thucydides.model.requirements;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WhenReadingRequirementsFromThePackageStructure {

    @BeforeEach
    void clearCaches() {
        ConfiguredEnvironment.reset();
        DefaultCapabilityTypes.instance().clear();
        RequirementCache.getInstance().clear();
    }

    @AfterAll
    static void afterAll() {
        ConfiguredEnvironment.reset();
        DefaultCapabilityTypes.instance().clear();
        RequirementCache.getInstance().clear();
    }

    @Test
    public void should_read_requirements_from_a_one_level_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "packagerequirements");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("capability"));
    }

    @Test
    public void should_read_requirements_from_a_two_level_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "twolevelpackagerequirements");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("theme"));
        assertThat(requirements.get(0).getChildren().get(0).getType(), equalTo("capability"));
        assertThat(requirements.get(0).getChildren().get(0).getChildren().get(0).getType(), equalTo("feature"));
    }


    @Test
    public void should_read_requirements_from_an_uneven_package_hierarchy() {

        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.test.root", "unevenpackagerequirements");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        PackageRequirementsTagProvider tagProvider = new PackageRequirementsTagProvider(environmentVariables).withCacheDisabled();

        List<Requirement> requirements = tagProvider.getRequirements();

        assertThat(requirements.get(0).getType(), equalTo("theme"));
        assertThat(requirements.get(0).getChildren().get(0).getType(), equalTo("capability"));
        assertThat(requirements.get(0).getChildren().get(0).getChildren().get(0).getType(), equalTo("feature"));
    }

}
