package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DisabledRequirementsStore implements RequirementsStore {
    @Override
    public void clear() {
    }

    @Override
    public Optional<List<Requirement>> read() throws IOException {
        return Optional.empty();
    }

    @Override
    public void write(List<Requirement> classpathRequirements) throws IOException {
    }
}
