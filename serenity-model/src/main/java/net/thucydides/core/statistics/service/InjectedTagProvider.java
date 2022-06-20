package net.thucydides.core.statistics.service;

import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allows tags to be added via the injected.tag system property.
 * They will be added to all of the test outcomes for a particular test run.
 */
public class InjectedTagProvider implements TagProvider, CoreTagProvider {

    EnvironmentVariables environmentVariables;

    public InjectedTagProvider() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public InjectedTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        String injectedTagValues = ThucydidesSystemProperty.INJECTED_TAGS.from(environmentVariables,"");

        if (injectedTagValues.isEmpty()) { return new HashSet<>(); }

        List<String> tags = Splitter.on(",").trimResults().splitToList(injectedTagValues);

        return tags.stream()
                .map(TestTag::withValue)
                .collect(Collectors.toSet());
    }
}
