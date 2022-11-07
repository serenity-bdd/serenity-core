package net.thucydides.core.model.featuretags;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StoryFileStrategy implements FeatureTagStrategy {

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {
        return Optional.of(story.asTag());
    }
}
