package net.serenitybdd.core.injectors;

import com.google.common.collect.Lists;
import net.serenitybdd.core.di.DependencyInjector;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Created by john on 26/03/2015.
 */
public class EnvironmentDependencyInjector implements DependencyInjector {

    private final EnvironmentVariables environmentVariables;
    private final SystemPropertiesConfiguration systemPropertiesConfiguration;

    public EnvironmentDependencyInjector() {
        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        systemPropertiesConfiguration = Injectors.getInjector().getInstance(SystemPropertiesConfiguration.class);
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


