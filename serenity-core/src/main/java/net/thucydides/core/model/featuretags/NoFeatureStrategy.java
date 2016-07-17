package net.thucydides.core.model.featuretags;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;

public class NoFeatureStrategy implements FeatureTagStrategy {

        @Override
        public Optional<TestTag> getFeatureTag(Story story, String path) {
            if ((story == null) && (path == null)) {
                return Optional.absent();
            }
            return Optional.fromNullable(story.asTag());
        }
    }