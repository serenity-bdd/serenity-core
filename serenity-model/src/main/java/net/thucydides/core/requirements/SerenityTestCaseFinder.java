package net.thucydides.core.requirements;

import net.thucydides.core.adapters.TestFramework;

/**
 * Created by john on 22/07/2015.
 */
public class SerenityTestCaseFinder {

    public boolean isSerenityTestCase(Class<?> testClass) {
        return TestFramework.support().isSerenityTestCase(testClass);
    }

}
