package net.thucydides.core.requirements.reports;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.RequirementsMerger;
import net.thucydides.core.requirements.RequirementsProvided;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementTree;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Merges and consolidates requirements hierarchies coming from several sources.
 * For example, if JUnit tests use a package hierarchy and Cucumber tests use a directory structure hierarchy.
 */
public class MultipleSourceRequirmentsOutcomeFactory implements RequirementsOutcomeFactory {

    private final List<RequirementsTagProvider> requirementsTagProviders;
    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final ReportNameProvider reportNameProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleSourceRequirmentsOutcomeFactory.class);

    public MultipleSourceRequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders,
                                                   IssueTracking issueTracking,
                                                   ReportNameProvider reportNameProvider) {
        this(requirementsTagProviders, issueTracking, Injectors.getInjector().getProvider(EnvironmentVariables.class).get(), reportNameProvider);
    }

    public MultipleSourceRequirmentsOutcomeFactory(List<RequirementsTagProvider> requirementsTagProviders,
                                                   IssueTracking issueTracking,
                                                   EnvironmentVariables environmentVariables,
                                                   ReportNameProvider reportNameProvider) {
        this.requirementsTagProviders = NewList.copyOf(requirementsTagProviders);
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
        this.reportNameProvider = reportNameProvider;
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {
        List<Requirement> allRequirements = new ArrayList<>();

        for (RequirementsTagProvider tagProvider : requirementsTagProviders) {
            allRequirements = new RequirementsMerger().merge(allRequirements, RequirementsProvided.by(tagProvider));
        }
        LOGGER.debug("Merged requirements set:{}{}", System.lineSeparator(), RequirementTree.withRequirements(allRequirements));
        return new RequirementsOutcomes(allRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                requirementsTagProviders,
                reportNameProvider);
    }


    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();
        return new RequirementsOutcomes(parentRequirement, childRequirements, testOutcomes, issueTracking,
                environmentVariables, requirementsTagProviders, reportNameProvider);
    }

}
