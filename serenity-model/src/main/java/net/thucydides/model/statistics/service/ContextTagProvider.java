package net.thucydides.model.statistics.service;

import com.google.common.base.Splitter;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.ContextIcon;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.requirements.CoreTagProvider;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_ADD_CONTEXT_TAG;

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

        if (StringUtils.isEmpty(testOutcome.getContext())) {
            return new HashSet<>();
        }

        if (!SERENITY_ADD_CONTEXT_TAG.booleanFrom(environmentVariables, true)) {
            return Collections.unmodifiableSet(new HashSet<>());
        }

        String contextLabelValue = ContextIcon.labelForOutcome(testOutcome);
        List<String> contextLabels = Splitter.on(",").omitEmptyStrings().splitToList(contextLabelValue);
        Set<TestTag> contextTags = new HashSet<>();

        contextTags.add(TestTag.withName(String.join(", ", contextLabels)).andType("context"));
        contextTags.addAll(
                contextLabels.stream()
                        .map(contextLabel -> TestTag.withName(contextLabel).andType("context"))
                        .collect(Collectors.toSet())
        );
        return contextTags;
    }
}
