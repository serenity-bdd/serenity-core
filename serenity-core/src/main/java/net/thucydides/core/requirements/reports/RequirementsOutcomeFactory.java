package net.thucydides.core.requirements.reports;

import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.model.Requirement;

/**
 * Find the requirements tree for a given set of test outcomes
 */
public interface RequirementsOutcomeFactory {
    RequirementsOutcomes buildRequirementsOutcomesFrom(TestOutcomes testOutcomes);
    RequirementsOutcomes buildRequirementsOutcomesFrom(Requirement parentRequirement, TestOutcomes testOutcomes);

}
