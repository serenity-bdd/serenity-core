package net.thucydides.model.statistics.service;

import net.serenitybdd.model.collect.NewSet;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.domain.features.ApplicationFeature;
import net.thucydides.model.requirements.CoreTagProvider;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.HashSet;
import java.util.Set;

/**
 * Legacy tag provider that builds tags based on the Feature/Story structure, if the @WithTag annotation is not used.
 * If the @WithTag annotation is used, @Feature classes will not be used.
 */
public class FeatureStoryTagProvider implements TagProvider, CoreTagProvider {

    private final EnvironmentVariables environmentVariables;

    public FeatureStoryTagProvider() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables() );
    }

    public FeatureStoryTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<>();
        addStoryTagIfPresent(testOutcome, tags);
        addFeatureTagIfPresent(testOutcome, tags);
        return NewSet.copyOf(tags);
    }

    private void addStoryTagIfPresent(TestOutcome testOutcome, Set<TestTag> tags) {
        Story story = testOutcome.getUserStory();
        if (story != null && shouldAddStoryTags()) {
            tags.add(story.asTag());
        }
    }

    private boolean shouldAddStoryTags() {
        return ThucydidesSystemProperty.USE_TEST_CASE_FOR_STORY_TAG.booleanFrom(environmentVariables, true);
    }

    private void addFeatureTagIfPresent(TestOutcome testOutcome, Set<TestTag> tags) {
        ApplicationFeature feature = testOutcome.getFeature();
        if (feature != null) {
            tags.add(TestTag.withName(feature.getName()).andType("feature"));
        }
    }
}
