package net.thucydides.core.requirements;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_LIST;
import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;


public abstract class BaseRequirementsService implements RequirementsService {
    protected List<Requirement> requirements;
    protected List<Release> releases;
    private Map<Requirement, List<Requirement>> requirementAncestors;

    protected final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRequirementsService.class);

    public BaseRequirementsService(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public abstract List<Requirement> getRequirements();

    public abstract List<? extends RequirementsTagProvider> getRequirementsTagProviders();

    public abstract Optional<ReleaseProvider> getReleaseProvider();

    public java.util.Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                java.util.Optional<Requirement> requirement = getParentRequirementOf(testOutcome, tagProvider);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return java.util.Optional.empty();
    }

    public java.util.Optional<Requirement> getRequirementFor(TestTag tag) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                java.util.Optional<Requirement> requirement = tagProvider.getRequirementFor(tag);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return Optional.empty();
    }

    public List<Requirement> getAncestorRequirementsFor(TestOutcome testOutcome) {
        for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {


            java.util.Optional<Requirement> requirement = getParentRequirementOf(testOutcome, tagProvider);
            if (requirement.isPresent()) {
                LOGGER.trace("Requirement found for test outcome " + testOutcome.getTitle() + "-" + testOutcome.getIssueKeys() + ": " + requirement);
                Optional<Requirement> matchingAncestor = matchingAncestorFor(requirement.get());
                if (matchingAncestor.isPresent()) {
                    return getRequirementAncestors().get(matchingAncestor.get());
                } else {
                    LOGGER.warn("Requirement without identified ancestors found test outcome " + testOutcome.getTitle() + "-" + testOutcome.getIssueKeys() + ": " + requirement);
                }
            }
        }
        return EMPTY_LIST;
    }

    Optional<Requirement> matchingAncestorFor(Requirement requirement) {
        return getRequirementAncestors().keySet().stream().filter(
                requirementKey -> requirementKey.matches(requirement)
        ).findFirst();
    }


    protected void indexRequirements() {
        requirementAncestors = new ConcurrentHashMap();
        for (Requirement requirement : requirements) {
            List<Requirement> requirementPath = NewList.of(requirement);
            requirementAncestors.put(requirement, NewList.of(requirement));
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }


    private void indexChildRequirements(List<Requirement> ancestors, List<Requirement> children) {
        for (Requirement requirement : children) {
            List<Requirement> requirementPath = new ArrayList(ancestors);
            requirementPath.add(requirement);
            requirementAncestors.put(requirement, NewList.copyOf(requirementPath));
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }

    private ReleaseManager releaseManager;

    private ReleaseManager getReleaseManager() {
        if (releaseManager == null) {
            ReportNameProvider defaultNameProvider = new ReportNameProvider(NO_CONTEXT, ReportType.HTML, this);
            releaseManager = new ReleaseManager(environmentVariables, defaultNameProvider, this);
        }
        return releaseManager;
    }


    private Map<Requirement, List<Requirement>> getRequirementAncestors() {
        if (requirementAncestors == null) {
            getRequirements();
        }
        return requirementAncestors;
    }

    private java.util.Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome, RequirementsTagProvider tagProvider) {

        java.util.Optional<Requirement> parentDefinedInTags = ParentRequirementsProvided.by(tagProvider).forOutcome(testOutcome);
        if (parentDefinedInTags.isPresent()) {
            java.util.Optional<Requirement> matchingIndexedParentRequirement = findMatchingIndexedRequirement(parentDefinedInTags.get());
            return matchingIndexedParentRequirement;
        }

        return java.util.Optional.empty();
    }

    private java.util.Optional<Requirement> findMatchingIndexedRequirement(Requirement requirement) {
        return AllRequirements.asStreamFrom(requirements)
                .filter(requirement::matches)
                .map(matchingRequirement -> mostPreciseOf(requirement, matchingRequirement))
                .findFirst();
//
//        for(Requirement indexedRequirement : AllRequirements.in(requirements)) {
//            if (requirement.matches(indexedRequirement)) {
//                return java.util.Optional.of(mostPreciseOf(requirement,indexedRequirement));
//            }
//        }
//        return java.util.Optional.empty();
    }

    private Requirement mostPreciseOf(Requirement thisRequirement, Requirement thatRequirement) {
        String thisParent = thisRequirement.getParent() != null ? thisRequirement.getParent() : "";
        String thatParent = thatRequirement.getParent() != null ? thatRequirement.getParent() : "";

        return (thatParent.length() >= thisParent.length()) ? thatRequirement : thisRequirement;
    }

    public List<Release> getReleasesFromRequirements() {
        if (releases == null) {
            if (getReleaseProvider().isPresent() && (getReleaseProvider().get().isActive())) {
                releases = getReleaseProvider().get().getReleases();
            } else {
                List<List<String>> releaseVersions = getReleaseVersionsFrom(getRequirements());
                releases = getReleaseManager().extractReleasesFrom(releaseVersions);
            }
        }
        return releases;
    }

    public List<String> getRequirementTypes() {

        return requirementTypesDefinedIn(getRequirements())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private Set<String> allTypesIn(List<Requirement> requirements) {
        Set<String> flattenedRequirements = new HashSet<>();
        requirements.forEach(
                requirement -> {
                    flattenedRequirements.add(requirement.getType());
                    if (requirement.getChildren() != null) {
                        flattenedRequirements.addAll(allTypesIn(requirement.getChildren()));
                    }
                }
        );
        return flattenedRequirements;
    }

    private Collection<String> requirementTypesDefinedIn(List<Requirement> requirements) {

        RequirementsConfiguration requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
        List<String> configuredRequirementTypes = requirementsConfiguration.getRequirementTypes();

        List<String> requirementTypes = new ArrayList<>();
        Set<String> allRequirementTypes = allTypesIn(requirements);
        // First add the configured requirements if we can find them
        for(String configuredRequirementType : configuredRequirementTypes) {
            if (allRequirementTypes.contains(configuredRequirementType)) {
                requirementTypes.add(configuredRequirementType);
            }
        }
        // Add any requirement types not specified in the configuration
        for (Requirement requirement : requirements) {
            if (!requirementTypes.contains(requirement.getType())) {
                requirementTypes.add(requirement.getType());
            }
            if (!requirement.getChildren().isEmpty()) {
                requirementTypes.addAll(childRequirementTypesDefinedIn(requirement.getChildren()));
            }
        }
        return requirementTypes;
    }


    private Collection<String> childRequirementTypesDefinedIn(List<Requirement> requirements) {

        List<String> requirementTypes = new ArrayList<>();

        // Add any requirement types not specified in the configuration
        for (Requirement requirement : requirements) {
            if (!requirementTypes.contains(requirement.getType())) {
                requirementTypes.add(requirement.getType());
            }
            if (!requirement.getChildren().isEmpty()) {
                requirementTypes.addAll(childRequirementTypesDefinedIn(requirement.getChildren()));
            }
        }
        return requirementTypes;
    }

    private Collection<TestTag> requirementTagsOfType(List<Requirement> requirements, List<String> tagTypes) {
        return AllRequirements.asStreamFrom(requirements)
                .map(requirement -> tagsWithTypes(requirement, tagTypes))
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private Stream<TestTag> tagsWithTypes(Requirement requirement, List<String> tagTypes) {
        return requirement.getTags()
                .stream()
                .filter(tag -> tagTypes.contains(tag.getType()));
    }

    @Override
    public List<String> getReleaseVersionsFor(TestOutcome testOutcome) {
        List<String> releases = new ArrayList(testOutcome.getVersions());
        for (Requirement parentRequirement : getAncestorRequirementsFor(testOutcome)) {
            releases.addAll(parentRequirement.getReleaseVersions());
        }
        return releases;
    }


    private List<List<String>> getReleaseVersionsFrom(List<Requirement> requirements) {
        List<List<String>> releaseVersions = new ArrayList();
        for (Requirement requirement : requirements) {
            releaseVersions.add(requirement.getReleaseVersions());
            releaseVersions.addAll(getReleaseVersionsFrom(requirement.getChildren()));
        }
        return releaseVersions;
    }

    @Override
    public boolean isRequirementsTag(TestTag tag) {
        return getRequirementTypes().contains(tag.getType());
    }

    @Override
    public Collection<TestTag> getTagsOfType(List<String> tagTypes) {
        return requirementTagsOfType(getRequirements(), tagTypes);
    }

    @Override
    public Collection<Requirement> getRequirementsWithTagsOfType(List<String> tagTypes) {
        return AllRequirements.asStreamFrom(requirements)
                .filter(requirement -> hasTagOfType(requirement, tagTypes))
                .collect(Collectors.toList());
    }


    private boolean hasTagOfType(Requirement requirement, List<String> tagTypes) {
        return requirement.getTags()
                .stream()
                .anyMatch(tag -> tagTypes.contains(tag.getType()));
    }


    public boolean containsEmptyRequirementWithTag(TestTag tag) {
        return AllRequirements.asStreamFrom(requirements)
                .anyMatch(
                        requirement -> (requirement.hasTag(tag) && requirement.containsNoScenarios())
                );
    }

    @Override
    public void resetRequirements() {
        requirements = null;
    }
}
