package net.thucydides.core.model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test paths need to use displayed names when used rather than package names.
 * This class keeps track of the relationships between test classes and display names,
 * so they can be retrieved later when determining the test path for a given test.
 */
public class TestClassHierarchy {
    private static final TestClassHierarchy INSTANCE = new TestClassHierarchy();

    private final Map<String, String> classPathToDisplayName = new ConcurrentHashMap<>();
    private final Map<String, String> idToDisplayName = new ConcurrentHashMap<>();
    private final Map<String, String> idToParent = new ConcurrentHashMap<>();

    public static TestClassHierarchy getInstance() {
        return INSTANCE;
    }

    /**
     * Keep track of relationships between test classes and display names such as in JUnit 5 tests
     */
    public void testSuiteStarted(String className,
                                 String uniqueIdentifier,
                                 String displayName,
                                 String parentUniqueIdentifier) {
        idToDisplayName.put(uniqueIdentifier, displayName);
        classPathToDisplayName.put(className, displayName);
        if (parentUniqueIdentifier != null) {
            idToParent.put(uniqueIdentifier, parentUniqueIdentifier);
        }
    }

    public Optional<String> displayNameFor(String path) {
        return Optional.ofNullable(classPathToDisplayName.get(path));
    }
}
