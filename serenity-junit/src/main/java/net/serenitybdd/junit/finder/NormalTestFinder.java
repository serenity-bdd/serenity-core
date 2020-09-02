package net.serenitybdd.junit.finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns all of the Thucydides classes under the specified package.
 */
public class NormalTestFinder extends TestFinder {
    public NormalTestFinder(final String rootPackage) {
        super(rootPackage);
    }

    @Override
    public List<Class<?>> getClasses() {
        return sorted(new ArrayList(getNormalTestClasses()));
    }

    @Override
    public int countTestMethods() {
        return getAllTestMethods().size();
    }

}
