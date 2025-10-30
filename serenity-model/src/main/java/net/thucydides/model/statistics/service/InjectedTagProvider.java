package net.thucydides.model.statistics.service;

import com.google.common.base.Splitter;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.requirements.CoreTagProvider;
import net.thucydides.model.util.EnvironmentVariables;

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
