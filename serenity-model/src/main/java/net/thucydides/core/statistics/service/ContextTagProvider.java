package net.thucydides.core.statistics.service;

import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_ADD_CONTEXT_TAG;

/**
 * Allows tags to be added via the injected.tag system property.
 * They will be added to all of the test outcomes for a particular test run.
 */
public class ContextTagProvider implements TagProvider, CoreTagProvider {

    EnvironmentVariables environmentVariables;

    public ContextTagProvider() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public ContextTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {

        if (StringUtils.isEmpty(testOutcome.getContext())) { return new HashSet<>(); }

        if (!THUCYDIDES_ADD_CONTEXT_TAG.booleanFrom(environmentVariables,true)) {
            return Collections.unmodifiableSet(new HashSet<>());
        }
        return NewSet.of(TestTag.withName(testOutcome.getContext()).andType("context"));
    }
}
