package serenitymodel.net.thucydides.core.requirements;

import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.requirements.model.Requirement;

import java.util.Optional;

public interface ParentRequirementProvider {
    Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome);
}
