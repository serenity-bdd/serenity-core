package net.thucydides.core.requirements;

import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Find the requirements hierarchy or the requirements associated with a given test outcome
 */
public interface RequirementsService extends ParentRequirementProvider {
    List<Requirement> getRequirements();

    Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome);

    Optional<Requirement> getRequirementFor(TestTag tag);

    boolean isRequirementsTag(TestTag tag);

    List<Requirement> getAncestorRequirementsFor(TestOutcome testOutcome);

    List<String> getReleaseVersionsFor(TestOutcome testOutcome);

    List<Release> getReleasesFromRequirements();

    List<String> getRequirementTypes();

    Collection<TestTag> getTagsOfType(List<String> tagTypes);

    Collection<Requirement> getRequirementsWithTagsOfType(List<String> tagTypes);

    boolean containsEmptyRequirementWithTag(TestTag tag);

    void resetRequirements();
}
