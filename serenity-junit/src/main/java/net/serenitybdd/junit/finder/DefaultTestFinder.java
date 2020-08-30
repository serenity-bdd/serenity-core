package net.serenitybdd.junit.finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns all of the Thucydides classes under the specified package.
 */
public class DefaultTestFinder extends TestFinder {
    public DefaultTestFinder(final String rootPackage) {
        super(rootPackage);
    }

    @Override
    public List<Class<?>> getClasses() {
        return sorted(new ArrayList(getAllTestClasses()));
    }

    @Override
    public int countTestMethods() {
        return getAllTestMethods().size();
    }

}
