package net.thucydides.model.domain.featuretags;

import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestTag;

import java.util.Optional;

public interface FeatureTagStrategy {
    Optional<TestTag> getFeatureTag(Story story, String path);
}
