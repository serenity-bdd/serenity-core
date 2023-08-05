package net.thucydides.model.domain.featuretags;

import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestTag;

import java.util.Optional;

public class NoFeatureStrategy implements FeatureTagStrategy {

        @Override
        public Optional<TestTag> getFeatureTag(Story story, String path) {
            if (story == null) { return Optional.empty(); }
            return Optional.ofNullable(story.asTag());
        }
    }
