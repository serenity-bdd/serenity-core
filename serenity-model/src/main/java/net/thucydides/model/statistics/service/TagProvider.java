package net.thucydides.model.statistics.service;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;

import java.util.Set;

public interface TagProvider {

    /**
     * Returns the tags associated with a given test outcome.
     * @param testOutcome the outcome for a specific test
     * @return the tags associated with a given test outcome.
     */
    Set<TestTag> getTagsFor(final TestOutcome testOutcome);
}
