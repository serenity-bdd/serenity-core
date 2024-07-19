package net.thucydides.model.requirements.reports;

import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.requirements.model.Requirement;

/**
 * Find the requirements tree for a given set of test outcomes
 */
public interface RequirementsOutcomeFactory {
    RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes);
    RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes);

}
