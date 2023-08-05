package net.thucydides.model.requirements;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.requirements.model.Requirement;

import java.util.Optional;

public interface ParentRequirementProvider {
    Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome);
}
