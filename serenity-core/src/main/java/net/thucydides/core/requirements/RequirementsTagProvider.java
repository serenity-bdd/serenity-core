package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.statistics.service.TagProvider;

import java.util.List;

/**
 *  A requirements provider returns the set of application requirements in a tree structure.
 *  Requirements are mapped to test outcomes using tags: a test outcome satisfying a given requirement needs to
 *  be tagged with the name of this requirement. So an implementation of this interface needs to do two things:
 *  return a list of top-level requirements (usually with nested child requirements, e.g. capabilities and features),
 *  and provide a way to determine what requirement tags need to be associated with each test outcome.
 *  The requirements provider implementation also needs to be listed in the corresponding META-INF/services file.
 */
public interface RequirementsTagProvider extends TagProvider {
    List<Requirement> getRequirements();
    Optional<Requirement> getParentRequirementOf(final TestOutcome testOutcome);
    Optional<Requirement> getParentRequirementOf(final Requirement requirement);
    Optional<Requirement> getRequirementFor(TestTag testTag);
}
