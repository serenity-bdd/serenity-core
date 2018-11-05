package net.thucydides.core.statistics.service;

import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.HashSet;
import java.util.Set;

/**
 * Legacy tag provider that builds tags based on the Feature/Story structure, if the @WithTag annotation is not used.
 * If the @WithTag annotation is used, @Feature classes will not be used.
 */
public class FeatureStoryTagProvider implements TagProvider, CoreTagProvider {

    private final EnvironmentVariables environmentVariables;

    public FeatureStoryTagProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
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
