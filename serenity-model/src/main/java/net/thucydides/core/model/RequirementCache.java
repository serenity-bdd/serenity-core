package net.thucydides.core.model;

import net.thucydides.core.requirements.model.Requirement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class RequirementCache {
    private List<Requirement> requirements;
    private final Map<Requirement, Requirement> requirementAliases;
    private final List<Requirement> flattenedRequirements;
    private final Map<String, String> requirementDisplayNames;
    private final Map<PathElements, Requirement> requirementsIndex;
    private final Map<String, Requirement> requirementsPathIndex;
    private final Map<TestTag, Requirement> requirementsByTag;

    private RequirementCache() {
        requirements = new ArrayList<>();
        requirementAliases = new ConcurrentHashMap<>();
        requirementDisplayNames = new ConcurrentHashMap<>();
        flattenedRequirements = new ArrayList<>();
        requirementsIndex = new ConcurrentHashMap<>();
        requirementsPathIndex = new ConcurrentHashMap<>();
        requirementsByTag = new ConcurrentHashMap<>();
    }

    public static RequirementCache getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void setRequirementAliases(Map<Requirement, Requirement> requirementAliases) {
        this.requirementAliases.putAll(requirementAliases);
    }

    public synchronized List<Requirement> getRequirements(Supplier<List<Requirement>> requirementFactory) {
        if (requirements.isEmpty()) {
            requirements = requirementFactory.get();
        }
        return requirements;
    }

    public void indexRequirements(Map<PathElements, Requirement> requirementsByPath) {
        this.requirementsIndex.putAll(requirementsByPath);
        requirementsByPath.forEach(
                (path, requirement) -> requirementsPathIndex.put(requirement.getPath(), requirement)
        );

    }

    public Map<PathElements, Requirement> getRequirementsIndex() {
        return requirementsIndex;
    }

    public Map<String, Requirement> getRequirementsPathIndex() {
        if (requirementsPathIndex.isEmpty()) {
            getRequirements();
        }
        return requirementsPathIndex;
    }

    public Requirement getRequirementsByTag(TestTag testTag, Function<TestTag, Requirement> requirementFinder) {
        if (!requirementsByTag.containsKey(testTag)) {
            Requirement matchingRequirement = requirementFinder.apply(testTag);
            if (matchingRequirement != null) {
                requirementsByTag.put(testTag, matchingRequirement);
            }
        }
        return requirementsByTag.get(testTag);

    }

    private static class SingletonHelper {
        private static final RequirementCache INSTANCE = new RequirementCache();
    }

    public synchronized void updateFlattenedRequirements(List<Requirement> requirements) {
        requirements.forEach(requirement -> {
                    if (!flattenedRequirements.contains(requirement)) flattenedRequirements.add(requirement);
                }
        );
    }

    public synchronized List<Requirement> getRequirements() {
        return requirements;
    }

    public Optional<String> getRequirementDisplayNameFor(String path) {
        return Optional.ofNullable(requirementDisplayNames.get(path));
    }

    public void setRequirementDisplayName(String requirementName, String displayName) {
        requirementDisplayNames.put(requirementName, displayName);
    }

    public Map<Requirement, Requirement> getRequirementAliases() {
        return requirementAliases;
    }

    public synchronized List<Requirement> getFlattenedRequirements() {
        return new ArrayList<>(flattenedRequirements);
    }
}

