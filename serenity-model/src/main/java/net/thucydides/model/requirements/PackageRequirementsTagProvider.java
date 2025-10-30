package net.thucydides.model.requirements;

import com.google.common.base.Splitter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.ScanResult;
import net.serenitybdd.annotations.Narrative;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.requirements.annotations.ClassInfoAnnotations;
import net.thucydides.model.requirements.classpath.LeafRequirementAdder;
import net.thucydides.model.requirements.classpath.NonLeafRequirementsAdder;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_TEST_ROOT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 * By default, the tests
 */
@Deprecated
public class PackageRequirementsTagProvider extends AbstractRequirementsTagProvider
        implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {

    private final EnvironmentVariables environmentVariables;

    private String rootPackage;

    private List<Requirement> requirements;

    private final RequirementsStore requirementsStore;

    List<String> requirementPaths;

    private final static Logger logger = LoggerFactory.getLogger(PackageRequirementsTagProvider.class);

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables,
                                          String rootPackage,
                                          RequirementsStore requirementsStore) {
        super(environmentVariables);
        this.environmentVariables = environmentVariables;
        this.rootPackage = rootPackage;
        this.requirementsStore = requirementsStore;
    }

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables, String rootPackage) {
        this(environmentVariables,
                rootPackage,
                (isEmpty(rootPackage) ?
                        new DisabledRequirementsStore() :
                        new FileSystemRequirementsStore(getRequirementsDirectory(ConfiguredEnvironment.getConfiguration().getOutputDirectory()),
                                rootPackage + "-package-requirements.json")));
    }

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this(environmentVariables, SERENITY_TEST_ROOT.from(environmentVariables));
    }

    public PackageRequirementsTagProvider() {
        this(ConfiguredEnvironment.getEnvironmentVariables());
    }

    private final List<Requirement> NO_REQUIREMENTS = new ArrayList<>();

    public void clear() {
        requirementsStore.clear();
    }

    public PackageRequirementsTagProvider withCacheDisabled() {
        return new PackageRequirementsTagProvider(environmentVariables, rootPackage, new DisabledRequirementsStore());
    }

    @Override
    public List<Requirement> getRequirements() {

        if (resolvedRootPackage() == null) {
            return NO_REQUIREMENTS;
        }

        if (requirements == null) {
            fetchRequirements();
        }

        return requirements;
    }

    private String resolvedRootPackage() {
        if ((rootPackage == null) && environmentVariables.aValueIsDefinedFor(SERENITY_TEST_ROOT)) {
            rootPackage = SERENITY_TEST_ROOT.from(environmentVariables);
        }
        return rootPackage;
    }

    private void fetchRequirements() {
        logger.debug("Loading requirements from package requirements at: " + resolvedRootPackage());

        requirements = reloadedRequirements().orElse(requirementsReadFromClasspath().orElse(NO_REQUIREMENTS));
    }

    private java.util.Optional<List<Requirement>> reloadedRequirements() {
        return java.util.Optional.empty();
//        try {
//            return requirementsStore.read();
//        } catch (IOException e) {
//            return java.util.Optional.empty();
//        }
    }

    private Optional<List<Requirement>> requirementsReadFromClasspath() {

        List<Requirement> classpathRequirements = null;

        try {
            List<String> requirementPaths = requirementPathsStartingFrom(resolvedRootPackage());
            int requirementsDepth = longestPathIn(requirementPaths);

            Set<Requirement> allRequirements = new HashSet<>();
            for (String path : requirementPaths) {
                addRequirementsDefinedIn(path, requirementsDepth, allRequirements);
            }

            allRequirements = removeChildrenFromTopLevelRequirementsIn(allRequirements);

            if (!allRequirements.isEmpty()) {
                classpathRequirements = new ArrayList<>(allRequirements);
                Collections.sort(classpathRequirements);
                requirementsStore.write(classpathRequirements);
            }

        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(classpathRequirements);
    }

    private Comparator<? super String> byDescendingPackageLength() {
        return (Comparator<String>) (o1, o2) -> {
            Integer o1Length = Splitter.on(".").splitToList(o1).size();
            Integer o2Length = Splitter.on(".").splitToList(o2).size();
            return o1Length.compareTo(o2Length);
        };
    }

//    private final Lock readingPaths = new ReentrantLock();

    private List<String> requirementPathsStartingFrom(String rootPackage) {

        if (isEmpty(rootPackage)) {
            return new ArrayList<>();
        }

        if (requirementPaths == null) {
//            readingPaths.lock();
            List<String> paths = requirementPathsFromClassesInPackage(rootPackage);
            paths.sort(byDescendingPackageLength());
            requirementPaths = paths;
//            readingPaths.unlock();
        }
        return requirementPaths;
    }

    private List<String> requirementPathsFromClassesInPackage(String rootPackage) {
        Set<Class> allClassesRecursive = findAllClassesUsingReflectionsLibrary(rootPackage);
        List<String> classRequirementNames = allClassesRecursive.stream()
                .filter(this::classRepresentsARequirementIn)
                .map(Class::getName)
                .map(className -> className.replaceAll("\\$","."))
                .collect(Collectors.toList());

        classRequirementNames.addAll(findPackagesWithNarrativeAnnotationIn(rootPackage));
        classRequirementNames.addAll(findClassesWithNarrativeAnnotationIn(rootPackage));
        return classRequirementNames;
    }

    private static final ConcurrentMap<String, ScanResult> SCAN_RESULT_CACHE = new ConcurrentHashMap<>();

    private Set<Class> findAllClassesUsingReflectionsLibrary(String packageName) {
        if (!SCAN_RESULT_CACHE.containsKey(packageName)) {
            SCAN_RESULT_CACHE.putIfAbsent(packageName, new ClassGraph().enableAllInfo().acceptPackages(packageName).scan());
        }
        ScanResult scanResult = SCAN_RESULT_CACHE.get(packageName);
        return scanResult.getAllClasses().stream()
                .map(classInfo -> classInfo.loadClass(true))
                .collect(Collectors.toSet());
    }

    private Set<String> findPackagesWithNarrativeAnnotationIn(String packageName) {
        if (!SCAN_RESULT_CACHE.containsKey(packageName)) {
            SCAN_RESULT_CACHE.putIfAbsent(packageName, new ClassGraph().enableAllInfo().acceptPackages(packageName).scan());
        }
        ScanResult scanResult = SCAN_RESULT_CACHE.get(packageName);
        return narrativePackagesIn(scanResult.getPackageInfo(packageName));
    }

    private Set<String> narrativePackagesIn(PackageInfo parentPackage) {
        Set<String> narrativePackages = new HashSet<>();
        if (parentPackage == null) {
            return new HashSet<>();
        }
        if  (parentPackage.hasAnnotation(Narrative.class)) {
            narrativePackages.add(parentPackage.getName());
        }
        for(PackageInfo childPackage : parentPackage.getChildren()) {
            narrativePackages.addAll(narrativePackagesIn(childPackage));
        }
        return narrativePackages;

    }

    private Set<String> findClassesWithNarrativeAnnotationIn(String packageName) {
        if (!SCAN_RESULT_CACHE.containsKey(packageName)) {
            SCAN_RESULT_CACHE.putIfAbsent(packageName, new ClassGraph().enableAllInfo().acceptPackages(packageName).scan());
        }
        ScanResult scanResult = SCAN_RESULT_CACHE.get(packageName);
        return  scanResult.getPackageInfo().stream().filter(packageInfo -> packageInfo.hasAnnotation(Narrative.class)).map(PackageInfo::getName).collect(Collectors.toSet());
    }

    private boolean classRepresentsARequirementIn(Class classInfo) {
        return (ClassInfoAnnotations.theClassDefinedIn(classInfo)
                .hasAnAnnotation(Narrative.class))
//                || (ClassInfoAnnotations.theClassDefinedIn(classInfo)
//                .hasAPackageAnnotation(net.serenitybdd.annotations.Narrative.class))
                || (ClassInfoAnnotations.theClassDefinedIn(classInfo).containsTests());
    }

    private Set<Requirement> removeChildrenFromTopLevelRequirementsIn(Set<Requirement> allRequirements) {
        Set<Requirement> prunedRequirements = new HashSet<>();
        for (Requirement requirement : allRequirements) {
            if (requirement.getParent() == null) {
                prunedRequirements.add(requirement);
            }
        }
        return prunedRequirements;
    }

    private int longestPathIn(List<String> requirementPaths) {
        int maxDepth = 0;
        for (String path : requirementPaths) {
            String pathWithoutRootPackage = path.replace(resolvedRootPackage() + ".", "");
            int pathDepth = Splitter.on(".").splitToList(pathWithoutRootPackage).size();
            if (pathDepth > maxDepth) {
                maxDepth = pathDepth;
            }
        }
        return min(maxDepth, requirementsConfiguration.getRequirementTypes().size());
    }

    private void addRequirementsDefinedIn(String path, int requirementsDepth, Collection<Requirement> allRequirements) {

        Requirement leafRequirement = LeafRequirementAdder.addLeafRequirementDefinedIn(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
                .usingRequirementTypes(getActiveRequirementTypes())
                .startingAt(resolvedRootPackage())
                .to(allRequirements);

        NonLeafRequirementsAdder.addParentsOf(leafRequirement)
                .in(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
                .startingAt(resolvedRootPackage())
                .to(allRequirements);

    }

    @Override
    public java.util.Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        return getTestCaseRequirementOf(testOutcome);
    }

    public java.util.Optional<Requirement> getTestCaseRequirementOf(TestOutcome testOutcome) {
        return AllRequirements.asStreamFrom(getRequirements())
                .filter(
                        requirement -> (requirement.asTag().isAsOrMoreSpecificThan(testOutcome.getUserStory().asTag()))
                ).findFirst();
    }

    @Override
    public java.util.Optional<Requirement> getRequirementFor(TestTag testTag) {
        Optional<Requirement> matching = AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirement.asTag().isAsOrMoreSpecificThan(testTag))
                .findFirst();

        if (matching.isPresent()) {
            return matching;
        }
        return uniqueRequirementWithName(testTag.getName());
    }

    private java.util.Optional<Requirement> uniqueRequirementWithName(String name) {
        return RequirementsList.of(getRequirements()).findByUniqueName(name);
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null) {
            return new HashSet<>();
        }

        Set<TestTag> tags = new HashSet<>();

        java.util.Optional<Requirement> matchingRequirement = getRequirementFor(testOutcome.getUserStory().asTag());

        if (matchingRequirement.isPresent()) {
            tags.add(matchingRequirement.get().asTag());

            Optional<Requirement> parent = parentOf(matchingRequirement.get());
            while (parent.isPresent()) {
                tags.add(parent.get().asTag());
                parent = parentOf(parent.get());
            }
        }
        return tags;
    }

    private Optional<Requirement> parentOf(Requirement child) {
        return AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirement.getChildren().contains(child))
                .findFirst();
    }

    private static File getRequirementsDirectory(File directory) {
        return new File(directory, "requirements");
    }

    public void clearCache() {
        requirementsStore.clear();
    }

    @Override
    public List<String> getActiveRequirementTypes() {
        List<String> allRequirementTypes = requirementsConfiguration.getRequirementTypes();

        int maxDepth = longestPathIn(requirementPathsStartingFrom(resolvedRootPackage()));

        return applicableRequirements(allRequirementTypes, maxDepth);
    }

    private List<String> applicableRequirements(List<String> allRequirementTypes, int maxDepth) {
        int startingLevel = max(allRequirementTypes.size() - maxDepth, 0);
        int endingLevel = allRequirementTypes.size();
        return allRequirementTypes.subList(startingLevel, endingLevel);
    }

    /**
     * Returns a list of all classes loaded by the given class loader.
     */
    public static List<Class<?>> getClassesLoadedBy(ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();
        // Get all loaded classes by iterating over the loaded class names
        for (String className : getLoadedClassNames(classLoader)) {
            try {
                // Load each class by name using the class loader
                Class<?> cls = Class.forName(className, false, classLoader);
                classes.add(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * Returns a list of the names of all classes loaded by the given class loader.
     */
    private static List<String> getLoadedClassNames(ClassLoader classLoader) {
        List<String> classNames = new ArrayList<>();
        // Get the class loader's parent, if any
        ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            // Recursively get the loaded class names of the parent class loader
            classNames.addAll(getLoadedClassNames(parent));
        }
        // Get the loaded class names of this class loader
        if (classLoader instanceof java.net.URLClassLoader) {
            java.net.URLClassLoader urlClassLoader = (java.net.URLClassLoader) classLoader;
            for (java.net.URL url : urlClassLoader.getURLs()) {
                try {
                    java.net.URLConnection connection = url.openConnection();
                    if (connection instanceof java.net.JarURLConnection) {
                        java.net.JarURLConnection jarConnection = (java.net.JarURLConnection) connection;
                        java.util.jar.JarFile jarFile = jarConnection.getJarFile();
                        for (java.util.jar.JarEntry entry : java.util.Collections.list(jarFile.entries())) {
                            if (entry.getName().endsWith(".class")) {
                                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - ".class".length());
                                classNames.add(className);
                            }
                        }
                    } else {
                        String path = url.getPath();
                        if (path.endsWith(".class")) {
                            String className = path.replace('/', '.').substring(0, path.length() - ".class".length());
                            classNames.add(className);
                        }
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return classNames;
    }


}
