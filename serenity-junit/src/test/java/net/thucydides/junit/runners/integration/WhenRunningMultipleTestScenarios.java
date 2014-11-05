package net.thucydides.junit.runners.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({RunningTestScenariosInBatches.class,
                     RunningTestScenariosInParallel.class})
public class WhenRunningMultipleTestScenarios {
}
