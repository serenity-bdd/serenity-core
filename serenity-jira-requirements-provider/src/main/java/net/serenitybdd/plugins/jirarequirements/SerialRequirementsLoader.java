package net.serenitybdd.plugins.jirarequirements;

import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by john on 28/01/2016.
 */
public class SerialRequirementsLoader implements RequirementsLoader {

    private final EnvironmentVariables environmentVariables;
    private final RequirementsAdaptor adaptor;
    private final JIRARequirementsProvider requirementsProvider;

    public SerialRequirementsLoader(EnvironmentVariables environmentVariables, JIRARequirementsProvider requirementsProvider) {
        this.environmentVariables = environmentVariables;
        this.adaptor = new RequirementsAdaptor(environmentVariables);
        this.requirementsProvider = requirementsProvider;
    }

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SerialRequirementsLoader.class);

    public List<Requirement> loadFrom(List<IssueSummary> rootRequirementIssues) {
        final List<Requirement> requirements = Collections.synchronizedList(new ArrayList<Requirement>());

        long t0 = System.currentTimeMillis();
        logger.debug("Loading {} requirements", rootRequirementIssues.size());
        for (final IssueSummary issueSummary : rootRequirementIssues) {
            Requirement requirement = adaptor.requirementFrom(issueSummary);
            List<Requirement> childRequirements = requirementsProvider.findChildrenFor(requirement, 0);
            requirements.add(requirement.withChildren(childRequirements));
        }
        logger.debug("Loading requirements done in {} ms", System.currentTimeMillis() - t0);
        return requirements;
    }
}
