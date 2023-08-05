package net.thucydides.model.domain.featuretags;

import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestTag;

import java.util.Optional;

public class StoryFileStrategy implements FeatureTagStrategy {

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {
        return Optional.of(story.asTag());
    }
}
