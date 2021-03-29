package serenitymodel.net.thucydides.core.model.featuretags;

import serenitymodel.net.thucydides.core.model.Story;
import serenitymodel.net.thucydides.core.model.TestTag;

import java.util.Optional;

public class FeatureFileStrategy implements FeatureTagStrategy {

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {

        return Optional.of(story.asTag());
    }
}