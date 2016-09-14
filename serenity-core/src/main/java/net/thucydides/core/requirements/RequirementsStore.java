package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import net.thucydides.core.requirements.model.Requirement;

import java.io.IOException;
import java.util.List;

public interface RequirementsStore {
    void clear();

    Optional<List<Requirement>> read() throws IOException;

    void write(List<Requirement> classpathRequirements) throws IOException;
}
