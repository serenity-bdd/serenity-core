package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.statistics.service.FeatureStoryTagProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.EMPTY_LIST;

public class RequirementsServiceImplementation implements RequirementsService {

    private List<RequirementsTagProvider> requirementsTagProviders;
    private List<Requirement> requirements;
    private List<Release> releases;
    private Map<Requirement, List<Requirement>> requirementAncestors;
    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final List<Requirement> NO_REQUIREMENTS = Lists.newArrayList();

    private static final List<String> LOW_PRIORITY_PROVIDERS =
            ImmutableList.of(PackageAnnotationBasedTagProvider.class.getCanonicalName(),
                    FeatureStoryTagProvider.class.getCanonicalName(),
                    FileSystemRequirementsTagProvider.class.getCanonicalName());

    public RequirementsServiceImplementation() {
        environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get() ;
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = newArrayList();
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                LOGGER.info("Reading requirements from " + tagProvider);
                requirements = tagProvider.getRequirements();
                if (!requirements.isEmpty()) {
                    break;
                }
            }
            requirements = addParentsTo(requirements);
            indexRequirements();
            LOGGER.info("Requirements found:" + requirements);
        }
        return requirements;
    }

    private List<Requirement> addParentsTo(List<Requirement> requirements) {
        return addParentsTo(requirements, null);
    }

    private List<Requirement> addParentsTo(List<Requirement> requirements, String parent) {
        List<Requirement> augmentedRequirements = Lists.newArrayList();
        for(Requirement requirement : requirements) {
            List<Requirement> children = requirement.hasChildren()
                    ? addParentsTo(requirement.getChildren(),requirement.getName()) : NO_REQUIREMENTS;
            augmentedRequirements.add(requirement.withParent(parent).withChildren(children));
        }
        return augmentedRequirements;
    }


    private void indexRequirements() {
        requirementAncestors = Maps.newHashMap();
        for (Requirement requirement : requirements) {
            List<Requirement> requirementPath = ImmutableList.of(requirement);
            requirementAncestors.put(requirement, ImmutableList.of(requirement));
            LOGGER.info("Requirement ancestors for:" + requirement + " = " + requirementPath);
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }

    ReleaseManager releaseManager;

    private ReleaseManager getReleaseManager() {
        if (releaseManager == null) {
            ReportNameProvider defaultNameProvider = new ReportNameProvider();
            releaseManager = new ReleaseManager(environmentVariables, defaultNameProvider);
        }
        return releaseManager;
    }


    private Map<Requirement, List<Requirement>> getRequirementAncestors() {
        if (requirementAncestors == null) {
            getRequirements();
        }
        return requirementAncestors;
    }

    private void indexChildRequirements(List<Requirement> ancestors, List<Requirement> children) {
        for (Requirement requirement : children) {
            List<Requirement> requirementPath = newArrayList(ancestors);
            requirementPath.add(requirement);
            requirementAncestors.put(requirement, ImmutableList.copyOf(requirementPath));
            LOGGER.info("Requirement ancestors for:" + requirement + " = " + requirementPath);
            indexChildRequirements(requirementPath, requirement.getChildren());
        }
    }


    @Override
    public Optional<Requirement> getParentRequirementFor(TestOutcome testOutcome) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                Optional<Requirement> requirement = getParentRequirementOf(testOutcome, tagProvider);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return Optional.absent();
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag tag) {

        try {
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                Optional<Requirement> requirement = tagProvider.getRequirementFor(tag);
                if (requirement.isPresent()) {
                    return requirement;
                }
            }
        } catch (RuntimeException handleTagProvidersElegantly) {
            LOGGER.error("Tag provider failure", handleTagProvidersElegantly);
        }
        return Optional.absent();
    }

    @Override
    public List<Requirement> getAncestorRequirementsFor(TestOutcome testOutcome) {
        for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
            Optional<Requirement> requirement = getParentRequirementOf(testOutcome, tagProvider);
            if (requirement.isPresent()) {
                LOGGER.info("Requirement found for test outcome " + testOutcome.getTitle() + "-" + testOutcome.getIssueKeys() + ": " + requirement);
                if (getRequirementAncestors().containsKey(requirement.get())) {
                    return getRequirementAncestors().get(requirement.get());
                } else {
                    LOGGER.warn("Requirement without identified ancestors found:" + requirement.get().getCardNumber());
                }
            }
        }
        return EMPTY_LIST;
    }

    Map<TestOutcome, Optional<Requirement>> requirementCache = Maps.newConcurrentMap();

    private Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome, RequirementsTagProvider tagProvider) {
        if (requirementCache.containsKey(testOutcome)) {
            return requirementCache.get(testOutcome);
        } else {
            Optional<Requirement> parentRequirement = findMatchingIndexedRequirement(tagProvider.getParentRequirementOf(testOutcome));
            requirementCache.put(testOutcome, parentRequirement);
            return parentRequirement;
        }
    }

    private Optional<Requirement> findMatchingIndexedRequirement(Optional<Requirement> requirement) {
        if (!requirement.isPresent()) {
            return requirement;
        }
        for(Requirement indexedRequirement : getAllRequirements()) {
            if (requirement.get().matches(indexedRequirement)) {
                return Optional.of(indexedRequirement);
            }
        }
        return Optional.absent();
    }

    @Override
    public List<String> getReleaseVersionsFor(TestOutcome testOutcome) {
        List<String> releases = newArrayList(testOutcome.getVersions());
        for (Requirement parentRequirement : getAncestorRequirementsFor(testOutcome)) {
            releases.addAll(parentRequirement.getReleaseVersions());
        }
        return releases;
    }

    @Override
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

    private Optional<ReleaseProvider> getReleaseProvider() {
        List<RequirementsTagProvider> requirementsTagProviders = getRequirementsTagProviders();
        for (RequirementsTagProvider provider : requirementsTagProviders) {
            if ((provider instanceof ReleaseProvider) && ((ReleaseProvider) provider).isActive()) {
                return Optional.of((ReleaseProvider) provider);
            }
        }
        return Optional.absent();
    }

    @Override
    public List<String> getRequirementTypes() {
        Set<String> requirementTypes = Sets.newHashSet();
        for(Requirement requirement : getAllRequirements()) {
            requirementTypes.add(requirement.getType());
        }
        return ImmutableList.copyOf(requirementTypes);
    }

    private List<List<String>> getReleaseVersionsFrom(List<Requirement> requirements) {
        List<List<String>> releaseVersions = newArrayList();
        for (Requirement requirement : requirements) {
            releaseVersions.add(requirement.getReleaseVersions());
            releaseVersions.addAll(getReleaseVersionsFrom(requirement.getChildren()));
        }
        return releaseVersions;
    }

    private List<RequirementsTagProvider> getRequirementsTagProviders() {
        if (requirementsTagProviders == null) {
            RequirementsProviderService requirementsProviderService = Injectors.getInjector().getInstance(RequirementsProviderService.class);
            requirementsTagProviders = reprioritizeProviders(active(requirementsProviderService.getRequirementsProviders()));
        }
        return requirementsTagProviders;
    }

    private List<RequirementsTagProvider> active(List<RequirementsTagProvider> requirementsProviders) {
        boolean useDirectoryBasedRequirements =
                environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.THUCYDIDES_USE_REQUIREMENTS_DIRECTORIES, true);

        if (useDirectoryBasedRequirements) {
            return requirementsProviders;
        } else {
            List<RequirementsTagProvider> activeRequirementsProviders = newArrayList();
            for (RequirementsTagProvider provider : requirementsProviders) {
                if (!(provider instanceof FileSystemRequirementsTagProvider)) {
                    activeRequirementsProviders.add(provider);
                }
            }
            return activeRequirementsProviders;
        }
    }


    private List<RequirementsTagProvider> reprioritizeProviders(List<RequirementsTagProvider> requirementsTagProviders) {
        List<RequirementsTagProvider> lowPriorityProviders = newArrayList();
        List<RequirementsTagProvider> prioritizedProviders = newArrayList();

        for (RequirementsTagProvider provider : requirementsTagProviders) {
            if (LOW_PRIORITY_PROVIDERS.contains(provider.getClass().getCanonicalName())) {
                lowPriorityProviders.add(provider);
            } else {
                prioritizedProviders.add(provider);
            }
        }
        prioritizedProviders.addAll(lowPriorityProviders);
        return prioritizedProviders;
    }

    public List<Requirement> getAllRequirements() {
        List<Requirement> allRequirements = newArrayList();
        addRequirementsFrom(getRequirements(), allRequirements);
        return allRequirements;
    }

    private void addRequirementsFrom(List<Requirement> requirements, List<Requirement> allRequirements) {
        allRequirements.addAll(requirements);
        for (Requirement requirement : requirements) {
            addRequirementsFrom(requirement.getChildren(), allRequirements);
        }
    }
}
