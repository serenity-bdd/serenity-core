package net.thucydides.core.requirements;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.requirements.model.Requirement;

import java.util.Optional;

public interface ParentRequirementProvider {
    Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome);
}
