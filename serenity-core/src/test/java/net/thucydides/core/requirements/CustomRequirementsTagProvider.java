package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CustomRequirementsTagProvider implements RequirementsTagProvider {
    @Override
    public List<Requirement> getRequirements() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        return Optional.absent();
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(Requirement requirement) {
        return Optional.absent();
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        return Optional.absent();
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return Collections.EMPTY_SET;
    }
}
