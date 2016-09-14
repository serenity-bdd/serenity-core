package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import net.thucydides.core.requirements.model.Requirement;

import java.io.IOException;
import java.util.List;

public class DisabledRequirementsStore implements RequirementsStore {
    @Override
    public void clear() {
    }

    @Override
    public Optional<List<Requirement>> read() throws IOException {
        return Optional.absent();
    }

    @Override
    public void write(List<Requirement> classpathRequirements) throws IOException {
    }
}
