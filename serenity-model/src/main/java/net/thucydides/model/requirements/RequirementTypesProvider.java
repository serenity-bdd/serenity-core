package net.thucydides.model.requirements;

import java.util.List;

/**
 * Provides a list of the requirement types used in the current project structure.
 * This can be different from the configured value, if the directory structure is not as deep.
 */
public interface RequirementTypesProvider {
    List<String> getActiveRequirementTypes();
}
