package net.thucydides.model.domain.results;

import net.thucydides.model.domain.TestResult;

/**
 * Created by john on 9/08/2015.
 */
public interface StepResultMergeStragegy {
    TestResult with(TestResult result);
}
