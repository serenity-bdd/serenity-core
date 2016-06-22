package net.thucydides.core.requirements.reports;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsMerger;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequirmentsOutcomeFactory {

    private final List<RequirementsTagProvider> requirementsTagProviders;
    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirmentsOutcomeFactory.class);

    public RequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders, IssueTracking issueTracking) {
        this(requirementsTagProviders, issueTracking, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public RequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders,
                                     IssueTracking issueTracking,
                                     EnvironmentVariables environmentVariables) {
        this.requirementsTagProviders = ImmutableList.copyOf(requirementsTagProviders);
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {
        List<Requirement> allRequirements = Lists.newArrayList();
        for(RequirementsTagProvider tagProvider : requirementsTagProviders) {
            LOGGER.debug("Merging requirements = " + tagProvider.getRequirements());
            allRequirements = new RequirementsMerger().merge(allRequirements, tagProvider.getRequirements());
        }
        LOGGER.debug("Merged requirements set = " + allRequirements);
        return new RequirementsOutcomes(allRequirements,
                                        testOutcomes, issueTracking, environmentVariables, requirementsTagProviders);
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();
        return new RequirementsOutcomes(parentRequirement, childRequirements, testOutcomes, issueTracking, environmentVariables, requirementsTagProviders);
    }

}
