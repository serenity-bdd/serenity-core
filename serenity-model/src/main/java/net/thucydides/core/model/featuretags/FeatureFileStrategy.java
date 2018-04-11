package net.thucydides.core.model.featuretags;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;

import java.util.Optional;

public class FeatureFileStrategy implements FeatureTagStrategy {

    private final CucumberParser cucumberParser = new CucumberParser();

    @Override
    public Optional<TestTag> getFeatureTag(Story story, String featureFilename) {

        return Optional.of(story.asTag());

//        File featureFile = FeatureOrStoryFile.forFeatureDescribedIn(featureFilename).asFile();
//
//        if (!featureFile.exists()) {
//            return Optional.of(TestTag.withName(humanize(featureFile.getName().replace(".feature", ""))).andType("feature"));
//        }
//
//        Optional<Narrative> narrative = cucumberParser.loadFeatureNarrative(featureFile);
//
//        String featureName = FeatureTitle.isDefinedIn(narrative) ?
//                FeatureTitle.leafRequirementDefinedIn(narrative) : humanize(featureFile.getName().replace(".feature", ""));
//
//        return Optional.of(TestTag.withName(featureName).andType("feature"));
    }
}