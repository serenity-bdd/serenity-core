package net.thucydides.junit.finder;

import com.google.common.collect.Lists;

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
        return sorted(Lists.newArrayList(getAllTestClasses()));
    }

    @Override
    public int countTestMethods() {
        return getAllTestMethods().size();
    }

}
