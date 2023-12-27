package net.thucydides.model.requirements.reports;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.RequirementsTagProvider;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @todo consider replacing with FileSystemRequirementsOutcomeFactory
 */
public class SerenityJsRequirementsOutcomeFactory implements RequirementsOutcomeFactory {
    private final EnvironmentVariables env;
    private final IssueTracking issueTracking;
    private final ReportNameProvider reportNameProvider;
    private final RequirementsTagProvider tagProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityJsRequirementsOutcomeFactory.class);


    public SerenityJsRequirementsOutcomeFactory(
            EnvironmentVariables env,
            IssueTracking issueTracking,
            ReportNameProvider reportNameProvider,
            RequirementsTagProvider tagProvider
    ) {
        this.env = env;
        this.issueTracking = issueTracking;
        this.reportNameProvider = reportNameProvider;
        this.tagProvider = tagProvider;
    }

    @Override
    public RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes) {

        List<Requirement> allRequirements = tagProvider.getRequirements();

        LOGGER.debug("Loaded requirements from the file system = " + allRequirements);

        return new RequirementsOutcomes(allRequirements,
                testOutcomes,
                issueTracking,
                env,
                NewList.<RequirementsTagProvider>of(tagProvider),
                reportNameProvider,
                tagProvider.getOverview().orElse("")
        );
    }

    @Override
    public RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes) {

        List<Requirement> childRequirements = parentRequirement.getChildren();

        return new RequirementsOutcomes(parentRequirement,
                childRequirements,
                testOutcomes,
                issueTracking,
                env,
                NewList.of(tagProvider),
                reportNameProvider,
                ""
        );
    }
}
