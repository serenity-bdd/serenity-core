package net.thucydides.core.model.featuretags;

import net.thucydides.core.requirements.model.Narrative;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by john on 7/07/2016.
 */
public class FeatureTitle {

    public static String definedIn(Optional<Narrative> narrative) {
        return narrative.get().getTitle().get();
    }

    public static boolean isDefinedIn(Optional<Narrative> narrative) {
        return (narrative.isPresent()
                && narrative.get().getTitle().isPresent()
                && isNotEmpty(narrative.get().getTitle().get())) ;
    }
}
