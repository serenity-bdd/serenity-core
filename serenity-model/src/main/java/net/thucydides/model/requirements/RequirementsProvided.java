package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RequirementsProvided {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final Iterable<Requirement> NO_REQUIREMENTS = new ArrayList<>();
    private static final List<Requirement> EMPTY_REQUIREMENTS = new ArrayList<>();

    public static Iterable<Requirement> by(RequirementsTagProvider tagProvider) {
        try {
            return tagProvider.getRequirements();
        } catch (Throwable requirementProviderFailedButDontLetThatStopUs) {
            LOGGER.warn("Failed to load requirements: " + requirementProviderFailedButDontLetThatStopUs.getMessage(),
                    requirementProviderFailedButDontLetThatStopUs);
            return NO_REQUIREMENTS;
        }
    }

    public static Stream<Requirement> asStream(RequirementsTagProvider tagProvider) {
        try {
            return tagProvider.getRequirements().stream();
        } catch (Throwable requirementProviderFailedButDontLetThatStopUs) {
            LOGGER.warn("Failed to load requirements: " + requirementProviderFailedButDontLetThatStopUs.getMessage(),
                    requirementProviderFailedButDontLetThatStopUs);
            return EMPTY_REQUIREMENTS.stream();
        }
    }
}
