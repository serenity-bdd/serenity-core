package net.thucydides.core.model.featuretags;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;

public class StoryFileStrategy implements FeatureTagStrategy {

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {

        System.out.println("Story name for " + featureFilename + " => " + story.asTag());
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