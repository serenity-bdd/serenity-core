package net.thucydides.core.model.failures;

import com.google.common.base.Splitter;
import io.cucumber.java.PendingException;
import net.serenitybdd.core.PendingStepException;
import net.serenitybdd.core.SkipStepException;
import net.serenitybdd.core.exceptions.CausesAssertionFailure;
import net.serenitybdd.core.exceptions.CausesCompromisedTestFailure;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FailureAnalysisConfiguration {

    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureAnalysisConfiguration.class);

    private final List<Class<?>> DEFAULT_FAILURE_TYPES = new ArrayList<>();
    {
        DEFAULT_FAILURE_TYPES.addAll(Arrays.asList(AssertionError.class, CausesAssertionFailure.class));
    }

    private final List<Class<?>> DEFAULT_COMPROMISED_TYPES = new ArrayList<>();
    {
        DEFAULT_COMPROMISED_TYPES.addAll(Arrays.asList(CausesCompromisedTestFailure.class));
    }

    private final List<Class<?>> DEFAULT_PENDING_TYPES = new ArrayList<>();
    {
        DEFAULT_PENDING_TYPES.addAll(Arrays.asList(PendingStepException.class, PendingException.class));
    }

    private final List<Class<?>> DEFAULT_SKIPPED_TYPES = new ArrayList<>();
    {
        DEFAULT_SKIPPED_TYPES.addAll(Arrays.asList(SkipStepException.class));
    }

    private final List<Class<?>> DEFAULT_ERROR_TYPES = new ArrayList<>();
    {
        DEFAULT_ERROR_TYPES.addAll(Arrays.asList(Error.class));
    }

    public FailureAnalysisConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<Class<?>> failureTypes() {
        List<Class<?>> failureTypes = new ArrayList<>(DEFAULT_FAILURE_TYPES);

        failureTypes.addAll(failureTypesDefinedIn(environmentVariables));

        failureTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(skippedTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));

        return failureTypes;
    }


    public List<Class<?>> compromisedTypes() {
        List<Class<?>> compromisedTypes = new ArrayList<>(DEFAULT_COMPROMISED_TYPES);

        compromisedTypes.addAll(compromisedTypesDefinedIn(environmentVariables));

        compromisedTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        compromisedTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        compromisedTypes.removeAll(skippedTypesDefinedIn(environmentVariables));
        compromisedTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return compromisedTypes;
    }

    public List<Class<?>> pendingTypes() {
        List<Class<?>> pendingTypes = new ArrayList<>(DEFAULT_PENDING_TYPES);
        pendingTypes.addAll(pendingTypesDefinedIn(environmentVariables));

        pendingTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        pendingTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));
        pendingTypes.removeAll(skippedTypesDefinedIn(environmentVariables));
        pendingTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return pendingTypes;
    }

    public List<Class<?>> skippedTypes() {
        List<Class<?>> skippedTypes = new ArrayList<>(DEFAULT_SKIPPED_TYPES);
        skippedTypes.addAll(skippedTypesDefinedIn(environmentVariables));

        skippedTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        skippedTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));
        skippedTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        skippedTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return skippedTypes;
    }

    public List<Class<?>> errorTypes() {
        List<Class<?>> errorTypes = new ArrayList<>(DEFAULT_ERROR_TYPES);
        errorTypes.addAll(errorTypesDefinedIn(environmentVariables));

        errorTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        errorTypes.removeAll(skippedTypesDefinedIn(environmentVariables));
        errorTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));
        errorTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return errorTypes;
    }

    private List<Class<?>> errorTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_ERROR_ON, environmentVariables);
    }

    private List<Class<?>> failureTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_FAIL_ON, environmentVariables);
    }

    private List<Class<?>> pendingTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_PENDING_ON, environmentVariables);
    }

    private List<Class<?>> compromisedTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_COMPROMISED_ON, environmentVariables);
    }

    private List<Class<?>> skippedTypesDefinedIn(EnvironmentVariables environmentVariables) {
        return typesDefinedIn(ThucydidesSystemProperty.SERENITY_SKIPPED_ON, environmentVariables);
    }

    private List<Class<?>> typesDefinedIn(ThucydidesSystemProperty typeListProperty, EnvironmentVariables environmentVariables) {

        List<Class<?>> definedTypes  = new ArrayList<>();
        List<String> classNames = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(typeListProperty.from(environmentVariables, ""));

        for(String className : classNames) {
            try {
                definedTypes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find error class: " + className + "(" + e.getMessage() + ")", e);
            }
        }
        return definedTypes;
    }
}
