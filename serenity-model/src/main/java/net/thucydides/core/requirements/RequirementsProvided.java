package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class RequirementsProvided {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final Iterable<Requirement> NO_REQUIREMENTS = new ArrayList<>();

    public static Iterable<Requirement> by(RequirementsTagProvider tagProvider) {
        try {
            return tagProvider.getRequirements();
        } catch (Throwable requirementProviderFailedButDontLetThatStopUs) {
            LOGGER.warn("Failed to load requirements: " + requirementProviderFailedButDontLetThatStopUs.getMessage(),
                    requirementProviderFailedButDontLetThatStopUs);
            return NO_REQUIREMENTS;
        }
    }
}
