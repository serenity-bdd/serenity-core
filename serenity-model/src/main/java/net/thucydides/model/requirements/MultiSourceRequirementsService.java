package net.thucydides.model.requirements;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.statistics.service.AnnotationBasedTagProvider;
import net.thucydides.model.statistics.service.FeatureStoryTagProvider;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultiSourceRequirementsService extends BaseRequirementsService implements RequirementsService {

    private List<RequirementsTagProvider> requirementsTagProviders;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsTagProvider.class);

    private static final List<String> LOW_PRIORITY_PROVIDERS =
            NewList.of(FileSystemRequirementsTagProvider.class.getCanonicalName(),
                             PackageAnnotationBasedTagProvider.class.getCanonicalName(),
                             AnnotationBasedTagProvider.class.getCanonicalName(),
                             FeatureStoryTagProvider.class.getCanonicalName()
                             );

    public MultiSourceRequirementsService() {
        super(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    @Override
    public List<Requirement> getRequirements() {
        if ((requirements == null) || (requirements.isEmpty())) {
            StopWatch stopWatch = StopWatch.createStarted();

            requirements = getRequirementsTagProviders().stream()
                    .parallel()
                    .flatMap(RequirementsProvided::asStream)
                    .collect(RequirementsCollector.merging());

            stopWatch.split();
            LOGGER.debug("Requirements loaded in {}", stopWatch.formatSplitTime());

            RequirementAncestry.addParentsTo(requirements);
            indexRequirements();

            stopWatch.split();
            LOGGER.debug("Requirements loaded and indexed in {}", stopWatch.formatTime());
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
        return Optional.empty();
    }

    public List<RequirementsTagProvider> getRequirementsTagProviders() {
        if (requirementsTagProviders == null) {
            RequirementsProviderService requirementsProviderService = ModelInfrastructure.getRequirementsProviderService();
            requirementsTagProviders = reprioritizeProviders(active(requirementsProviderService.getRequirementsProviders()));
        }
        return requirementsTagProviders;
    }

    private List<RequirementsTagProvider> active(List<RequirementsTagProvider> requirementsProviders) {
        boolean useDirectoryBasedRequirements =
                EnvironmentSpecificConfiguration.from(environmentVariables).getBooleanProperty(ThucydidesSystemProperty.SERENITY_USE_REQUIREMENTS_DIRECTORIES,true);

        if (useDirectoryBasedRequirements) {
            return requirementsProviders;
        } else {
            List<RequirementsTagProvider> activeRequirementsProviders = new ArrayList<>();
            for (RequirementsTagProvider provider : requirementsProviders) {
                if (!(provider instanceof FileSystemRequirementsTagProvider)) {
                    activeRequirementsProviders.add(provider);
                }
            }
            return activeRequirementsProviders;
        }
    }

    private List<RequirementsTagProvider> reprioritizeProviders(List<RequirementsTagProvider> requirementsTagProviders) {
        Map<String,RequirementsTagProvider> lowPriorityProviders = new HashMap<>();
        List<RequirementsTagProvider> prioritizedProviders = new ArrayList<>();

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

    @Override
    public void addRequirementTagsTo(TestOutcome outcome) {
        this.getRequirementsTagProviders().forEach(provider -> provider.addRequirementTagsTo(outcome));
    }
}
