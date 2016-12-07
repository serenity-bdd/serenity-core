package net.serenitybdd.core.injectors;

import com.google.common.collect.Lists;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Created by john on 26/03/2015.
 */
public class EnvironmentDependencyInjector implements DependencyInjector {

    private final EnvironmentVariables environmentVariables;
    private final Configuration systemPropertiesConfiguration;

    public EnvironmentDependencyInjector() {
        environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        systemPropertiesConfiguration = ConfiguredEnvironment.getConfiguration();
    }

    public void injectDependenciesInto(Object target) {
        List<Field> environmentVariableFields = matchingFieldsIn(target, EnvironmentVariables.class);
        for(Field environmentVariableField : environmentVariableFields) {
            injectEnvironmentVariables(environmentVariableField, target);
        }

        List<Field> sysConfigVariableFields = matchingFieldsIn(target, Configuration.class);
        for(Field sysConfigVariableField : sysConfigVariableFields) {
            injectSysConfigVariables(sysConfigVariableField, target);
        }

    }

    @Override
    public void reset() {}

    private void injectEnvironmentVariables(Field field, Object target) {
        try {
            field.setAccessible(true);
            if (field.get(target) == null) {
                field.set(target, environmentVariables);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instanciate environmentVariables in " + target);
        }
    }

    private void injectSysConfigVariables(Field field, Object target) {
        try {
            field.setAccessible(true);
            if (field.get(target) == null) {
                field.set(target, systemPropertiesConfiguration);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instanciate systemPropertiesConfiguration in " + target);
        }
    }

    private List<Field> matchingFieldsIn(Object target, Class fieldClass) {
        Set<Field> allFields = Fields.of(target.getClass()).allFields();
        List<Field> matchingFields = Lists.newArrayList();
        for(Field field : allFields) {
            if (fieldClass.isAssignableFrom(field.getType())) {
                matchingFields.add(field);
            }
        }
        return matchingFields;
    }
}


