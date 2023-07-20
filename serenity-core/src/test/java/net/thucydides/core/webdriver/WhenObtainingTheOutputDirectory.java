package net.thucydides.core.webdriver;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static net.thucydides.core.util.FileSeparatorUtil.changeSeparatorIfRequired;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WhenObtainingTheOutputDirectory {

    SystemPropertiesConfiguration configuration;
    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @After
    public void cleanup() {
        ConfiguredEnvironment.reset();
    }

    @Test
    public void the_default_output_directory_is_in_the_default_maven_site_directory() {
        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is(changeSeparatorIfRequired("target/site/serenity")));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_if_the_maven_output_directory_is_overridden_as_a_relative_path() {
        environmentVariables.setProperty("project.build.directory", "subproject");
        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is(changeSeparatorIfRequired("subproject/target/site/serenity")));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_if_the_maven_output_directory_is_overridden_as_an_absolute_path() {
        environmentVariables.setProperty("project.build.directory", "/myproject/subproject");
        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is(changeSeparatorIfRequired("/myproject/subproject/target/site/serenity")));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_if_the_maven_site_output_directory_is_overridden() {
        environmentVariables.setProperty("project.reporting.OutputDirectory", "custom-reports-directory");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is(changeSeparatorIfRequired("custom-reports-directory/serenity")));
    }

    @Test
    public void the_default_output_directory_can_be_overriden_using_a_thucydides_system_property() {
        environmentVariables.setProperty("serenity.outputDirectory", "thucydides-reports");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), is("thucydides-reports"));
    }

    @Test
    public void an_absolute_thucydides_system_property_always_takes_priority() {
        environmentVariables.setProperty("project.build.directory", "build");
        environmentVariables.setProperty("project.reporting.OutputDirectory", "custom-reports-directory");

        String absolutePath = (SystemUtils.IS_OS_WINDOWS)? "C:\\thucydides-reports" : "/thucydides-reports";

        environmentVariables.setProperty("serenity.outputDirectory", absolutePath);
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(),  is(absolutePath));
    }

    @Test
    public void a_relative_thucydides_system_property_uses_the_maven_build_dir() {
        environmentVariables.setProperty("project.build.directory", "build");
        environmentVariables.setProperty("project.reporting.OutputDirectory", "custom-reports-directory");
        environmentVariables.setProperty("serenity.outputDirectory", "thucydides-reports");
        ConfiguredEnvironment.updateConfiguration(environmentVariables);

        File outputDirectory = configuration.getOutputDirectory();

        assertThat(outputDirectory.getPath(), Matchers.anyOf(is("build/thucydides-reports"),
                                                             is("build\\thucydides-reports")));
    }
}
