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
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AggregateRequirementsService extends BaseRequirementsService implements RequirementsService {

    private List<RequirementsTagProvider> requirementsTagProviders;

    public AggregateRequirementsService(EnvironmentVariables environmentVariables,
                                        RequirementsTagProvider... tagProviders) {
        super(environmentVariables);
        this.requirementsTagProviders = NewList.of(tagProviders);
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRequirementsService.class);

    @Override
    public List<Requirement> getRequirements() {
        if ((requirements == null) || (requirements.isEmpty())) {
            StopWatch stopWatch = StopWatch.createStarted();

            requirements = getRequirementsTagProviders().stream()
                    .parallel()
                    .flatMap(RequirementsProvided::asStream)
                    .collect(Collectors.toCollection(MergedRequirementList::new));

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
        return requirementsTagProviders;
    }

    @Override
    public void addRequirementTagsTo(TestOutcome outcome) {
        this.getRequirementsTagProviders().forEach(provider -> provider.addRequirementTagsTo(outcome));
    }
}
