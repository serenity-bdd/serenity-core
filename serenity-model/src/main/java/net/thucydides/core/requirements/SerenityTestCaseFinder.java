package net.thucydides.core.requirements;

import net.thucydides.core.util.JUnitAdapter;

/**
 * Created by john on 22/07/2015.
 */
public class SerenityTestCaseFinder {

    public boolean isSerenityTestCase(Class<?> testClass) {
        return JUnitAdapter.isSerenityTestCase(testClass);
    }

}
