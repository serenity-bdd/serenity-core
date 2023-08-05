package net.thucydides.model.requirements;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.requirements.model.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ParentRequirementsProvided {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private final RequirementsTagProvider tagProvider;

    public ParentRequirementsProvided(RequirementsTagProvider tagProvider) {
        this.tagProvider = tagProvider;
    }

    public static ParentRequirementsProvided by(RequirementsTagProvider tagProvider) {
        return new ParentRequirementsProvided(tagProvider);
    }

    public Optional<Requirement> forOutcome(TestOutcome testOutcome) {
        try {
            return tagProvider.getParentRequirementOf(testOutcome);
        } catch (Throwable requirementProviderFailedButDontLetThatStopUs) {
            LOGGER.warn("Failed to load requirements: " + requirementProviderFailedButDontLetThatStopUs.getMessage(),
                    requirementProviderFailedButDontLetThatStopUs);
            return Optional.empty();
        }
    }
}
