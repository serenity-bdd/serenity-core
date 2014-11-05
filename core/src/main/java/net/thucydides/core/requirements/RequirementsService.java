package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;

public interface RequirementsService {
    List<Requirement> getRequirements();

    Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome);

    Optional<Requirement> getRequirementFor(TestTag tag);

    List<Requirement> getAncestorRequirementsFor(TestOutcome testOutcome);

    List<String> getReleaseVersionsFor(TestOutcome testOutcome);

    List<Release> getReleasesFromRequirements();

    List<String> getRequirementTypes();
}
