package net.thucydides.core.model.featuretags;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryFileStrategy implements FeatureTagStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoryFileStrategy.class);

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {

        LOGGER.debug("Story name for {} => {}", featureFilename, story.asTag());
        return Optional.of(story.asTag());
//        File featureFile = FeatureOrStoryFile.forStoryDescribedIn(featureFilename).asFile();
//
//        Optional<Narrative> narrative = storyReader.narrativeLoadedFrom(featureFile, ".story");
//
//        String featureName = FeatureTitle.isDefinedIn(narrative) ?
//                FeatureTitle.leafRequirementDefinedIn(narrative) : humanize(featureFile.getName().replace(".story", ""));
//
//        return Optional.of(TestTag.withName(featureName).andType("story"));
    }
}