package net.thucydides.model.domain;

import net.thucydides.model.requirements.model.Requirement;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RequirementCache {
    private volatile List<Requirement> requirements; // made volatile for double-checked locking
    private final List<Requirement> flattenedRequirements;
    private final Map<String, Requirement> requirementsPathIndex;
    private final Map<TestTag, Requirement> requirementsByTag;
    private final Set<Requirement> flattenedRequirementsSet; // to avoid list scanning

    private RequirementCache() {
        flattenedRequirements = new ArrayList<>();
        flattenedRequirementsSet = new HashSet<>();
        requirementsPathIndex = new HashMap<>();
        requirementsByTag = new HashMap<>();
    }

    public void clear() {
        requirements = null;
        flattenedRequirements.clear();
        flattenedRequirementsSet.clear();
        requirementsPathIndex.clear();
        requirementsByTag.clear();
    }

    public static RequirementCache getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public List<Requirement> getRequirements(Supplier<List<Requirement>> requirementFactory) {
        if (requirements == null) {
            requirements = requirementFactory.get();
        }
        return requirements;
    }

    public void indexRequirements(Map<PathElements, Requirement> requirementsByPath) {
        requirementsByPath.forEach(
                (path, requirement) -> requirementsPathIndex.put(requirement.getPath(), requirement)
        );
    }

    public Map<String, Requirement> getRequirementsPathIndex() {
        return requirementsPathIndex;
    }

    public Requirement getRequirementsByTag(TestTag testTag, Function<TestTag, Requirement> requirementFinder) {
        return getRequirementsByTag(testTag, requirementFinder, false);
    }

    public Requirement getRequirementsByTag(TestTag testTag, Function<TestTag, Requirement> requirementFinder, boolean forceLoad) {
        if (!requirementsByTag.containsKey(testTag) || (null == requirementsByTag.get(testTag) && forceLoad)) {
            requirementsByTag.put(testTag, requirementFinder.apply(testTag));
        }
        return requirementsByTag.get(testTag);
//        return requirementsByTag.computeIfAbsent(testTag, requirementFinder);
    }

    private static class SingletonHelper {
        private static final RequirementCache INSTANCE = new RequirementCache();
    }

    public void updateFlattenedRequirements(List<Requirement> requirements) {
        requirements.forEach(requirement -> {
            if (!flattenedRequirementsSet.contains(requirement)) {
                flattenedRequirements.add(requirement);
                flattenedRequirementsSet.add(requirement);
            }
        });
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public List<Requirement> getFlattenedRequirements() {
        return new ArrayList<>(flattenedRequirements);
    }
}

