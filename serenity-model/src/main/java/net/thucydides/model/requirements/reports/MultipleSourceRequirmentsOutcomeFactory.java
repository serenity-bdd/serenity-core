package net.thucydides.model.requirements.reports;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.RequirementsMerger;
import net.thucydides.model.requirements.RequirementsProvided;
import net.thucydides.model.requirements.RequirementsTagProvider;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.model.RequirementTree;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        this(requirementsTagProviders, issueTracking, SystemEnvironmentVariables.currentEnvironmentVariables(), reportNameProvider);
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

        LOGGER.debug("Loading requirements from tag providers :" + requirementsTagProviders);

        for (RequirementsTagProvider tagProvider : requirementsTagProviders) {
            allRequirements = new RequirementsMerger().merge(allRequirements, RequirementsProvided.by(tagProvider));
        }

        Optional<String> overview = requirementsTagProviders.stream()
                                                            .map(RequirementsTagProvider::getOverview)
                                                            .filter(Optional::isPresent)
                                                            .map(overviewText -> overviewText.get())
                                                            .findFirst();

        LOGGER.debug("LOADED REQUIREMENTS:{}{}", System.lineSeparator(), RequirementTree.withRequirements(allRequirements));

        return new RequirementsOutcomes(allRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                requirementsTagProviders,
                reportNameProvider,
                overview.orElse(""));
    }


    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();

        return new RequirementsOutcomes(parentRequirement, childRequirements, testOutcomes, issueTracking,
                environmentVariables, requirementsTagProviders, reportNameProvider, "");
    }

}
