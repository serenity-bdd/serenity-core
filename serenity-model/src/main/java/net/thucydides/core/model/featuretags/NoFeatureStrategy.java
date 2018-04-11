package net.thucydides.core.model.featuretags;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;

import java.util.Optional;

public class NoFeatureStrategy implements FeatureTagStrategy {

        @Override
        public Optional<TestTag> getFeatureTag(Story story, String path) {
            if (story == null) { return Optional.empty(); }
            return Optional.ofNullable(story.asTag());
        }
    }