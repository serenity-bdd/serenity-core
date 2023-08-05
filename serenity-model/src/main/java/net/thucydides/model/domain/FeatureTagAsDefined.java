package net.thucydides.model.domain;

import net.thucydides.model.domain.featuretags.FeatureFileStrategy;
import net.thucydides.model.domain.featuretags.FeatureTagStrategy;
import net.thucydides.model.domain.featuretags.NoFeatureStrategy;
import net.thucydides.model.domain.featuretags.StoryFileStrategy;
import net.thucydides.model.requirements.model.FeatureType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by john on 6/07/2016.
 */
public class FeatureTagAsDefined {

    private final static Map<FeatureType, FeatureTagStrategy> FEATURE_STRATEGY_MAP = new HashMap();
    static {
        FEATURE_STRATEGY_MAP.put(FeatureType.FEATURE, new FeatureFileStrategy());
        FEATURE_STRATEGY_MAP.put(FeatureType.STORY, new StoryFileStrategy());
        FEATURE_STRATEGY_MAP.put(FeatureType.UNDEFINED, new NoFeatureStrategy());

    }

    public static Optional<TestTag> in(Story story, String path) {
        return FEATURE_STRATEGY_MAP.get(featureTypeFor(story, path)).getFeatureTag(story, path);
    }

    private static FeatureType featureTypeFor(Story story, String path) {
        if (path != null && path.endsWith(".feature")) {
            return FeatureType.FEATURE;
        }
        if ((story != null) && (story.getType().equalsIgnoreCase("feature"))) {
            return FeatureType.FEATURE;
        }
        if ((story != null) && (story.getType().equalsIgnoreCase("story"))) {
            return FeatureType.STORY;
        }
        if (path != null && path.endsWith(".story")) {
            return FeatureType.STORY;
        }
        return FeatureType.UNDEFINED;
    }
}
