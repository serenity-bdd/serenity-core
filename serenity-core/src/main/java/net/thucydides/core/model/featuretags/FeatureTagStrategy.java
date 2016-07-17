package net.thucydides.core.model.featuretags;

import com.google.common.base.Optional;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;

public interface FeatureTagStrategy {
    Optional<TestTag> getFeatureTag(Story story, String path);
}