package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.statistics.service.AnnotationBasedTagProvider;
import net.thucydides.core.statistics.service.FeatureStoryTagProvider;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class MultiSourceRequirementsService extends BaseRequirementsService implements RequirementsService {

    private List<RequirementsTagProvider> requirementsTagProviders;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final List<String> LOW_PRIORITY_PROVIDERS =
            ImmutableList.of(FileSystemRequirementsTagProvider.class.getCanonicalName(),
                             PackageAnnotationBasedTagProvider.class.getCanonicalName(),
                             AnnotationBasedTagProvider.class.getCanonicalName(),
                             FeatureStoryTagProvider.class.getCanonicalName()
                             );

    public MultiSourceRequirementsService() {
        super(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    @Override
    public List<Requirement> getRequirements() {
        RequirementsMerger merger = new RequirementsMerger();

        if ((requirements == null) || (requirements.isEmpty())) {
            requirements = newArrayList();
            for (RequirementsTagProvider tagProvider : getRequirementsTagProviders()) {
                LOGGER.trace("Reading requirements from " + tagProvider);
                List<Requirement> newRequirements = tagProvider.getRequirements();
                requirements = merger.merge(requirements, newRequirements);
            }
            requirements = addParentsTo(requirements);
            indexRequirements();
            LOGGER.debug("Requirements found:" + requirements);
        }
        return requirements;
    }

    public Optional<ReleaseProvider> getReleaseProvider() {
        List<RequirementsTagProvider> requirementsTagProviders = getRequirementsTagProviders();
        for (RequirementsTagProvider provider : requirementsTagProviders) {
            if ((provider instanceof ReleaseProvider) && ((ReleaseProvider) provider).isActive()) {
                return Optional.of((ReleaseProvider) provider);
            }
        }
        return Optional.absent();
    }

    public List<RequirementsTagProvider> getRequirementsTagProviders() {
        if (requirementsTagProviders == null) {
            RequirementsProviderService requirementsProviderService = Injectors.getInjector().getInstance(RequirementsProviderService.class);
            requirementsTagProviders = reprioritizeProviders(active(requirementsProviderService.getRequirementsProviders()));
        }
        return requirementsTagProviders;
    }

    private List<RequirementsTagProvider> active(List<RequirementsTagProvider> requirementsProviders) {
        boolean useDirectoryBasedRequirements =
                ThucydidesSystemProperty.THUCYDIDES_USE_REQUIREMENTS_DIRECTORIES.booleanFrom(environmentVariables, true);

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
        Map<String,RequirementsTagProvider> lowPriorityProviders = Maps.newHashMap();
        List<RequirementsTagProvider> prioritizedProviders = newArrayList();

        for (RequirementsTagProvider provider : requirementsTagProviders) {
            if (LOW_PRIORITY_PROVIDERS.contains(provider.getClass().getCanonicalName())) {
                lowPriorityProviders.put(provider.getClass().getCanonicalName(), provider);
            } else {
                prioritizedProviders.add(provider);
            }
        }
        addLowPriorityProviders(lowPriorityProviders, prioritizedProviders);
        return prioritizedProviders;
    }

    private void addLowPriorityProviders(Map<String, RequirementsTagProvider> lowPriorityProviders, List<RequirementsTagProvider> prioritizedProviders) {
        for(String lowPriorityProvider : LOW_PRIORITY_PROVIDERS) {
            if (lowPriorityProviders.containsKey(lowPriorityProvider)) {
                prioritizedProviders.add(lowPriorityProviders.get(lowPriorityProvider));
            }
        }
    }
}
