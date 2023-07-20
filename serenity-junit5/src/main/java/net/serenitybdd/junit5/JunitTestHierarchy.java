package net.serenitybdd.junit5;

import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestIdentifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JunitTestHierarchy {
    private static final JunitTestHierarchy INSTANCE = new JunitTestHierarchy();

    private final Map<String, String> classPathToDisplayName = new ConcurrentHashMap<>();
    private final Map<String, String> idToDisplayName = new ConcurrentHashMap<>();
    private final Map<String, String> idToParent = new ConcurrentHashMap<>();

    public static JunitTestHierarchy getInstance() {
        return INSTANCE;
    }

    public void testSuiteStarted(TestIdentifier testIdentifier) {
        idToDisplayName.put(testIdentifier.getUniqueId(), testIdentifier.getDisplayName());
        if (testIdentifier.getSource().isPresent() && testIdentifier.getSource().get() instanceof ClassSource) {
            String className = ((ClassSource) testIdentifier.getSource().get()).getClassName();
            classPathToDisplayName.put(className, testIdentifier.getDisplayName());
        }
        testIdentifier.getParentId().ifPresent(
                parentId -> idToParent.put(testIdentifier.getUniqueId(), parentId)
        );

    }

}
