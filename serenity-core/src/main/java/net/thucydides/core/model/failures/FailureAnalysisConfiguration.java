package net.thucydides.core.model.failures;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cucumber.api.PendingException;
import net.serenitybdd.core.PendingStepException;
import net.serenitybdd.core.exceptions.CausesAssertionFailure;
import net.serenitybdd.core.exceptions.CausesCompromisedTestFailure;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FailureAnalysisConfiguration {

    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureAnalysisConfiguration.class);

    private final List<Class<?>> DEFAULT_FAILURE_TYPES = Lists.newArrayList();
    {
        DEFAULT_FAILURE_TYPES.addAll(ImmutableList.of(AssertionError.class, CausesAssertionFailure.class));
    }

    private final List<Class<?>> DEFAULT_COMPROMISED_TYPES = Lists.newArrayList();
    {
        DEFAULT_COMPROMISED_TYPES.addAll(ImmutableList.of(CausesCompromisedTestFailure.class));
    }

    private final List<Class<?>> DEFAULT_PENDING_TYPES = Lists.newArrayList();
    {
        DEFAULT_PENDING_TYPES.addAll(ImmutableList.of(PendingStepException.class, PendingException.class));
    }

    private final List<Class<?>> DEFAULT_ERROR_TYPES = Lists.newArrayList();
    {
        DEFAULT_ERROR_TYPES.addAll(ImmutableList.of(Error.class));
    }

    public FailureAnalysisConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<Class<?>> failureTypes() {
        List<Class<?>> failureTypes = Lists.newArrayList(DEFAULT_FAILURE_TYPES);

        failureTypes.addAll(failureTypesDefinedIn(environmentVariables));

        failureTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        failureTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));

        return failureTypes;
    }


    public List<Class<?>> compromisedTypes() {
        List<Class<?>> compromisedTypes = Lists.newArrayList(DEFAULT_COMPROMISED_TYPES);

        compromisedTypes.addAll(compromisedTypesDefinedIn(environmentVariables));

        compromisedTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        compromisedTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
        compromisedTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return compromisedTypes;
    }

    public List<Class<?>> pendingTypes() {
        List<Class<?>> pendingTypes = Lists.newArrayList(DEFAULT_PENDING_TYPES);
        pendingTypes.addAll(pendingTypesDefinedIn(environmentVariables));

        pendingTypes.removeAll(errorTypesDefinedIn(environmentVariables));
        pendingTypes.removeAll(compromisedTypesDefinedIn(environmentVariables));
        pendingTypes.removeAll(failureTypesDefinedIn(environmentVariables));

        return pendingTypes;
    }

    public List<Class<?>> errorTypes() {
        List<Class<?>> errorTypes = Lists.newArrayList(DEFAULT_ERROR_TYPES);
        errorTypes.addAll(errorTypesDefinedIn(environmentVariables));

        errorTypes.removeAll(pendingTypesDefinedIn(environmentVariables));
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

    private List<Class<?>> typesDefinedIn(ThucydidesSystemProperty typeListProperty, EnvironmentVariables environmentVariables) {

        List<Class<?>> definedTypes  = Lists.newArrayList();
        List<String> classNames = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(typeListProperty.from(environmentVariables, ""));

        for(String className : classNames) {
            try {
                definedTypes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find error class: " + className);
            }
        }
        return definedTypes;
    }
}
