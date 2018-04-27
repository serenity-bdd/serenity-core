package net.thucydides.core.requirements.reports;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileSystemRequirmentsOutcomeFactory implements RequirementsOutcomeFactory {

    private final IssueTracking issueTracking;
    private final EnvironmentVariables environmentVariables;
    private final FileSystemRequirementsTagProvider tagProvider;
    private final ReportNameProvider reportNameProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemRequirmentsOutcomeFactory.class);

    public FileSystemRequirmentsOutcomeFactory(EnvironmentVariables environmentVariables,
                                               IssueTracking issueTracking,
                                               ReportNameProvider reportNameProvider,
                                               String rootDirectoryPath) {
        this.issueTracking = issueTracking;
        this.environmentVariables = environmentVariables;
        this.tagProvider = new FileSystemRequirementsTagProvider(environmentVariables, rootDirectoryPath);
        this.reportNameProvider = reportNameProvider;

    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {
        List<Requirement> allRequirements = tagProvider.getRequirements();
        LOGGER.debug("Loaded requirements from file system = " + allRequirements);
        return new RequirementsOutcomes(allRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                NewList.<RequirementsTagProvider>of(tagProvider),
                reportNameProvider);
    }

    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement,
                                                              TestOutcomes testOutcomes) {
        List<Requirement> childRequirements = parentRequirement.getChildren();
        return new RequirementsOutcomes(parentRequirement,
                childRequirements,
                testOutcomes,
                issueTracking,
                environmentVariables,
                NewList.of(tagProvider),
                reportNameProvider);
    }

}
