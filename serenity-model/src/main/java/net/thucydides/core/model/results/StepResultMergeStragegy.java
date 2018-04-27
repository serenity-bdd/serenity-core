package net.thucydides.core.model.results;

import net.thucydides.core.model.TestResult;

/**
 * Created by john on 9/08/2015.
 */
public interface StepResultMergeStragegy {
    TestResult with(TestResult result);
}
