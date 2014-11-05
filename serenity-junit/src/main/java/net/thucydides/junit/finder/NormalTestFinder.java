package net.thucydides.junit.finder;

import com.google.common.collect.Lists;

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
        return sorted(Lists.newArrayList(getNormalTestClasses()));
    }

    @Override
    public int countTestMethods() {
        return getAllTestMethods().size();
    }

}
