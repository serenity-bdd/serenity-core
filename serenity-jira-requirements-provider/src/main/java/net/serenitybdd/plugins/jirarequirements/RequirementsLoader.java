package net.serenitybdd.plugins.jirarequirements;

import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.thucydides.model.requirements.model.Requirement;

import java.util.List;

/**
 * Created by john on 28/01/2016.
 */
public interface RequirementsLoader {
    List<Requirement> loadFrom(List<IssueSummary> rootRequirementIssues);
}
