package net.thucydides.model.requirements.reports;

import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.AggregateRequirementsService;
import net.thucydides.model.requirements.BaseRequirementsService;
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.model.requirements.RequirementsTagProvider;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileSystemRequirmentsOutcomeFactory implements RequirementsOutcomeFactory {

    private final static String EMPTY_OVERVIEW = "";

    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final BaseRequirementsService requirementsService;
    private final ReportNameProvider reportNameProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemRequirmentsOutcomeFactory.class);

    public FileSystemRequirmentsOutcomeFactory(final EnvironmentVariables environmentVariables,
                                               final IssueTracking issueTracking,
                                               final ReportNameProvider reportNameProvider,
                                               final String rootDirectoryPath) {
        this(
                environmentVariables,
                issueTracking,
                reportNameProvider,
                new AggregateRequirementsService(
                        environmentVariables,
                        new FileSystemRequirementsTagProvider(rootDirectoryPath, environmentVariables)
                )
        );
    }

    public FileSystemRequirmentsOutcomeFactory(final EnvironmentVariables environmentVariables,
                                               final IssueTracking issueTracking,
                                               final ReportNameProvider reportNameProvider,
                                               final BaseRequirementsService requirementsService) {
        this.environmentVariables = environmentVariables;
        this.issueTracking = issueTracking;
        this.reportNameProvider = reportNameProvider;
        this.requirementsService = requirementsService;
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {
        List<Requirement> allRequirements = requirementsService.getRequirements();
        LOGGER.debug("Loaded requirements from file system = " + allRequirements);
        return new RequirementsOutcomes(allRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                requirementsService.getRequirementsTagProviders(),
                reportNameProvider,
                getOverview()
        );
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement,
                                                              TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();
        return new RequirementsOutcomes(parentRequirement,
                childRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                requirementsService.getRequirementsTagProviders(),
                reportNameProvider,
                EMPTY_OVERVIEW);
    }

    private String getOverview() {
        // fixme: this is a bit of a hacky way to retrieve the overview that could potentially be improved
        for (RequirementsTagProvider provider : requirementsService.getRequirementsTagProviders()) {
            if (provider instanceof FileSystemRequirementsTagProvider) {
                return provider.getOverview().orElse(EMPTY_OVERVIEW);
            }
        }

        return EMPTY_OVERVIEW;
    }
}
