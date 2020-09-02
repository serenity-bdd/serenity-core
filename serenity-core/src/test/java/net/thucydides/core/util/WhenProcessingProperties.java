package net.thucydides.core.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenProcessingProperties {

	@Test
	public void environment_variables_references_are_expanded() {
        Properties properties = new Properties();
        properties.setProperty("serenity.outputDirectory", "${CUSTOM_ENVIRONMENT_VARIABLE}");
        Map<String,String> environment_variables = new HashMap<String,String>();
        environment_variables.put("CUSTOM_ENVIRONMENT_VARIABLE", "/custom/folder/reports");

        PropertiesUtil.expandPropertyAndEnvironmentReferences(environment_variables, properties);

        assertThat(properties.get("serenity.outputDirectory"), is("/custom/folder/reports"));
	}

    @Test
    public void environment_variables_references_can_be_embedded_in_other_variables() {
        Properties properties = new Properties();
        properties.setProperty("serenity.outputDirectory", "${CUSTOM_ENVIRONMENT_VARIABLE}/out");
        Map<String,String> environment_variables = new HashMap<String,String>();
        environment_variables.put("CUSTOM_ENVIRONMENT_VARIABLE", "/custom/folder/reports");

        PropertiesUtil.expandPropertyAndEnvironmentReferences(environment_variables, properties);

        assertThat(properties.get("serenity.outputDirectory"), is("/custom/folder/reports/out"));
    }

    @Test
    public void system_properties_references_are_expanded() {
        Properties properties = new Properties();
        properties.setProperty("serenity.outputDirectory", "${custom.system.property}");
        System.setProperty("custom.system.property", "/custom/folder/reports");

        PropertiesUtil.expandPropertyAndEnvironmentReferences(new HashMap<>(), properties);

        assertThat(properties.get("serenity.outputDirectory"), is("/custom/folder/reports"));
    }

    @Test
    public void system_properties_can_be_embedded() {
        Properties properties = new Properties();
        properties.setProperty("serenity.outputDirectory", "${user.dir}/out");

        PropertiesUtil.expandPropertyAndEnvironmentReferences(new HashMap<>(), properties);

        String actualUserDir = System.getProperty("user.dir");

        assertThat(properties.get("serenity.outputDirectory"), is(actualUserDir + "/out"));
    }

}
