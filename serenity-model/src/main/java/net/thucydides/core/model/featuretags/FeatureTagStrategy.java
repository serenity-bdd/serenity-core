package net.thucydides.core.model.featuretags;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestTag;

import java.util.Optional;

public interface FeatureTagStrategy {
    Optional<TestTag> getFeatureTag(Story story, String path);
}