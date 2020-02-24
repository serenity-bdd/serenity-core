package net.thucydides.core.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenReadingEnvironmentVariables {

    @Test
    public void should_read_environment_variable_from_system() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables(System.getProperties(), getEnvVars());
        String value = environmentVariables.getValue("JAVA_HOME");
        assertThat(value, is(not(nullValue())));
    }

    enum LocalEnvProperties {JAVA_HOME, USER_DIR, DOES_NOT_EXIST}

    @Test
    public void should_read_environment_variable_from_system_using_an_enum() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables(System.getProperties(), getEnvVars());
        String value = environmentVariables.getValue(LocalEnvProperties.JAVA_HOME);
        assertThat(value, is(not(nullValue())));
    }

    private Map<String, String> getEnvVars() {
        Map<String, String> environmentVars = new HashMap<String, String>(System.getenv());
        if (!environmentVars.containsKey("JAVA_HOME")) {
            environmentVars.put("JAVA_HOME", "sample");
        }
        return environmentVars;
    }

    @Test
    public void should_read_environment_variable_from_system_with_a_default_using_an_enum() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue(LocalEnvProperties.DOES_NOT_EXIST,"default");
        assertThat(value, is("default"));
    }


    @Test
    public void should_return_null_for_inexistant_environment_variable() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void should_return_default_for_inexistant_environment_variable_if_specified() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST","NO_ENVIRONMENT_DEFINED");
        assertThat(value, is("NO_ENVIRONMENT_DEFINED"));
    }

    enum LocalSystemProperties {
        SOME_INTEGER_PROPERTY() {
            @Override
            public String toString() {
                return "some.integer.property";
            }
        },
        SOME_BOOLEAN_PROPERTY() {
            @Override
            public String toString() {
                return "some.boolean.property";
            }
        },
        SOME_PROPERTY() {
            @Override
            public String toString() {
                return "some.property";
            }
        },
        SOME_UNDEFINED_PROPERTY() {
            @Override
            public String toString() {
                return "some.undefined.property";
            }
        }
    }

    @Test
    public void should_read_integer_system_properties_from_the_system() {
        System.setProperty("some.integer.property","10");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        int value = environmentVariables.getPropertyAsInteger("some.integer.property",5);
        assertThat(value, is(10));
    }

    @Test
    public void should_read_integer_system_properties_from_the_system_via_an_enum() {
        System.setProperty("some.integer.property","10");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        int value = environmentVariables.getPropertyAsInteger(LocalSystemProperties.SOME_INTEGER_PROPERTY,5);
        assertThat(value, is(10));
    }

    @Test
    public void should_read_boolean_system_properties_from_the_system() {
        System.setProperty("some.boolean.property","true");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        boolean value = environmentVariables.getPropertyAsBoolean("some.boolean.property",false);
        assertThat(value, is(true));
    }
    
    @Test
    public void should_read_boolean_system_properties_from_the_system_with_a_default() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();

        assertThat(environmentVariables.getPropertyAsBoolean("some.unknown.boolean.property",false), is(false));
        assertThat(environmentVariables.getPropertyAsBoolean("some.unknown.boolean.property",true), is(true));

    }

    @Test
    public void should_read_boolean_system_properties_from_the_system_via_an_enum() {
        System.setProperty("some.boolean.property","true");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        boolean value = environmentVariables.getPropertyAsBoolean(LocalSystemProperties.SOME_BOOLEAN_PROPERTY,false);
        assertThat(value, is(true));
    }

    @Test
    public void should_read_integer_system_properties_with_default_from_the_system() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        int value = environmentVariables.getPropertyAsInteger("some.default.integer.property",5);
        assertThat(value, is(5));
    }

    @Test
    public void should_read_system_properties_from_the_system() {
        System.setProperty("some.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("some.property");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_system_properties_from_the_system_with_an_enum() {
        System.setProperty("some.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty(LocalSystemProperties.SOME_PROPERTY);
        assertThat(value, is("some.value"));
    }


    @Test
    public void should_read_default_value_for_a_system_property_from_the_system_with_an_enum() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty(LocalSystemProperties.SOME_UNDEFINED_PROPERTY, "default");
        assertThat(value, is("default"));
    }

    @Test
    public void should_be_able_to_clear_a_set_system_properties() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        environmentVariables.setProperty("my.property","SomeValue");
        environmentVariables.clearProperty("my.property");

        assertThat(System.getProperty("my.property"), is(nullValue()));
    }


    @Test
    public void should_read_system_properties_from_the_system_via_an_enum() {
        System.setProperty("some.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty(LocalSystemProperties.SOME_PROPERTY);
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_system_properties_with_default_values_from_the_system() {
        System.setProperty("some.other.property","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("some.other.property", "NO_ENVIRONMENT_DEFINED");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_default_system_properties_with_default_values_from_the_system() {
        System.clearProperty("another.property");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("another.property", "NO_ENVIRONMENT_DEFINED");
        assertThat(value, is("NO_ENVIRONMENT_DEFINED"));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("a.property","value");
        assertThat(environmentVariables.getProperty("a.property"), is("value"));
    }

    @Test
    public void mock_environment_variables_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getProperty("property","default"), is("default"));
    }

    @Test
    public void mock_environment_variables_allow_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("integer.property","30");
        assertThat(environmentVariables.getPropertyAsInteger("integer.property", 0), is(30));
    }

    @Test
    public void mock_environment_variables_allow_default_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsInteger("integer.property", 10), is(10));
    }

    @Test
    public void mock_environment_variables_allow_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("boolean.property","true");
        assertThat(environmentVariables.getPropertyAsBoolean("boolean.property", false), is(true));
    }

    @Test
    public void mock_environment_variables_allow_default_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsBoolean("property", true), is(true));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_environment_values_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setValue("env","value");
        assertThat(environmentVariables.getValue("env"), is("value"));
    }

    @Test
    public void mock_environment_values_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getValue("env","default"), is("default"));
    }

    @Test
    public void environment_variable_sets_can_be_safely_copied() {

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        environmentVariables.setProperty("some.property", "VALUE");

        EnvironmentVariables environmentVariablesCopy = environmentVariables.copy();
        environmentVariablesCopy.setProperty("some.property", "ANOTHER VALUE");

        assertThat(environmentVariables.getProperty("some.property"), is("VALUE"));
    }

}
