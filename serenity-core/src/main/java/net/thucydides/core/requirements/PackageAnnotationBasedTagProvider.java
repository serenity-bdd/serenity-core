package net.thucydides.core.requirements;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.annotations.NarrativeFinder;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT;
import static net.thucydides.core.reflection.ClassFinder.loadClasses;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * A requirements Provider that reads requirement from class or package annotation.
 * The root package is defined using {@link ThucydidesSystemProperty#THUCYDIDES_TEST_ROOT}
 * It is recommended to change the root package if the {@link FileSystemRequirementsTagProvider} is used.
 *
 * @see Narrative
 * @see ThucydidesSystemProperty#THUCYDIDES_TEST_ROOT
 */
public class PackageAnnotationBasedTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider {
    private static final String DOT_REGEX = "\\.";
    private final static List<String> SUPPORTED_SUFFIXES = ImmutableList.of("story", "feature");

    private final Configuration configuration;
    private final RequirementPersister persister;
    private String rootPackage;
    SortedMap<String, Requirement> requirementsByPath = null;

    public PackageAnnotationBasedTagProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public PackageAnnotationBasedTagProvider(EnvironmentVariables vars) {
        super(vars);
        configuration = new SystemPropertiesConfiguration(environmentVariables);
        rootPackage = THUCYDIDES_TEST_ROOT.from(environmentVariables, rootDirectory);
        persister = new RequirementPersister(getRequirementsDirectory(), rootPackage);
    }

    private File getRequirementsDirectory() {
        return new File(configuration.getOutputDirectory(),"requirements");
    }

    @Override
    public List<Requirement> getRequirements() {
        return ImmutableList.copyOf(rootRequirementsIn(getRequirementsByPath().values()));
    }

    Map<Requirement, String> getRequirementPaths() {
        Map<Requirement, String> requirementPaths = Maps.newHashMap();
        for (String path : getRequirementsByPath().keySet()) {
            requirementPaths.put(getRequirementsByPath().get(path), path);
        }
        return requirementPaths;
    }

    private List<Requirement> rootRequirementsIn(Collection<Requirement> allRequirements) {
        List<Requirement> rootRequirements = Lists.newArrayList();
        for (Requirement requirement : allRequirements) {
            if (requirement.getParent() == null) {
                rootRequirements.add(requirement);
            }
        }
        return rootRequirements;
    }

    private Map<String, Requirement> getRequirementsByPath() {
        if (requirementsByPath == null) {
            requirementsByPath = loadRequirementsByPath();
        }
        return requirementsByPath;
    }

    private SortedMap<String, Requirement> loadRequirementsByPath() {
        List<Class<?>> classes = loadClassesFromPath();
        return (classes.isEmpty()) ? loadPersistedRequirements() : loadRequirementsFromClasses(classes);
    }

    private SortedMap<String, Requirement> loadPersistedRequirements() {
        try {
            return persister.read();
        } catch (IOException e) {
            e.printStackTrace();
            return new TreeMap<>();
        }
    }

    private SortedMap<String, Requirement> loadRequirementsFromClasses(List<Class<?>> classes) {
        SortedMap<String, Requirement> requirementMap = Maps.newTreeMap();
        int maxDepth = maximumClassDepth(classes, rootPackage);

        for (Class candidateClass : classes) {
            addRequirementTo(requirementMap, candidateClass, maxDepth);
        }
        addChildrenTo(requirementMap);
        persistRequirementsAsJSON(requirementMap);
        return requirementMap;
    }


    private void addChildrenTo(SortedMap<String, Requirement> requirementsByPath) {
        Set<String> paths = requirementsByPath.keySet();
        for (String path : paths) {
            Requirement requirement = requirementsByPath.get(path);
            List<Requirement> childRequirements = directChildrenOf(requirement).in(requirementsByPath.values());
            requirement.setChildren(childRequirements);
        }
    }

    private RequirementChildLocator directChildrenOf(Requirement requirement) {
        return new RequirementChildLocator(requirement);
    }

    private void addRequirementTo(Map<String, Requirement> requirementsByPath,
                                  Class candidateClass,
                                  int maxDepth) {

        String fullRequirementName = getFullRequirementPath(candidateClass);

        String[] packageNames = fullRequirementName.split(DOT_REGEX);
        String currentPath = "";
        Requirement parentRequirement = null;
        for (int level = 0; level < packageNames.length; level++) {
            currentPath = (currentPath.isEmpty()) ? packageNames[level] : Joiner.on(".").join(currentPath, packageNames[level]);
            String defaultRequirementType = getDefaultType(level, maxDepth);
            Requirement currentRequirement;
            if (requirementsByPath.containsKey(currentPath)) {
                currentRequirement = requirementsByPath.get(currentPath);
            } else {
                if (level < packageNames.length - 1) {
                    currentRequirement = newParentRequirement(currentPath, parentRequirement, packageNames[level], level, defaultRequirementType);
                    requirementsByPath.put(currentPath, currentRequirement);
                } else {
                    currentRequirement = newRequirement(candidateClass, currentPath, parentRequirement, packageNames[level], level, defaultRequirementType);
                    String fullPath = getFullRequirementPath(candidateClass);
                    requirementsByPath.put(fullPath, currentRequirement);
                }
            }
            parentRequirement = currentRequirement;
        }
    }

    private String getFullRequirementPath(Class candidateClass) {
        return candidateClass.getName().replace(rootPackage + ".", "").replace(".package-info", "");
    }

    private int maximumClassDepth(List<Class<?>> classes, String rootPackage) {
        int maxDepth = 0;
        for (Class candidateClass : classes) {
            int pathDepth = pathDepth(rootPackage, candidateClass.getPackage().getName());
            maxDepth = (pathDepth > maxDepth) ? pathDepth : maxDepth;
        }
        return maxDepth;
    }

    private int pathDepth(String rootPackage, String path) {
        int maxDepth = 0;
        if (path.startsWith(rootPackage)) {
            String localPath = path.replace(rootPackage, "");
            maxDepth = StringUtils.split(localPath, ".").length;
        }
        return maxDepth;
    }


    private Requirement newParentRequirement(String requirementPath,
                                             Requirement parentRequirement,
                                             String packageName,
                                             int level,
                                             String defaultRequirementType) {
        String requirementTitle = packageName;
        String requirementType = defaultRequirementType;
        String narrativeText = "";
        String cardNumber = "";

        Class candidateClass = null;

        Optional<Narrative> narrative = Optional.absent();
        try {
            candidateClass = Class.forName(rootPackage + "." + requirementPath + ".package-info");
            narrative = NarrativeFinder.forClass(candidateClass);
        } catch (ClassNotFoundException ignore) {
        }

        Requirement newRequirement = getRequirement(candidateClass, packageName, level, requirementTitle, requirementType, narrativeText, cardNumber, narrative);
        if (parentRequirement != null) {
            newRequirement = newRequirement.withParent(parentRequirement.getName());
        }
        return newRequirement;
    }

    private Requirement newRequirement(Class candidateClass,
                                       String currentPath,
                                       Requirement parentRequirement,
                                       String packageName,
                                       int level,
                                       String defaultRequirementType) {
        String requirementTitle = packageName;
        String requirementType = defaultRequirementType;
        String narrativeText = "";
        String cardNumber = "";

        Optional<Narrative> narrative = NarrativeFinder.forClass(candidateClass);


        Requirement newRequirement = getRequirement(candidateClass, packageName, level, requirementTitle, requirementType, narrativeText, cardNumber, narrative);
        if (parentRequirement != null) {
            newRequirement = newRequirement.withParent(parentRequirement.getName());
        }
        return newRequirement;
    }

    private Requirement getRequirement(Class candidateClass, String packageName, int level, String requirementTitle, String requirementType, String narrativeText, String cardNumber, Optional<Narrative> narrative) {
        if (narrative.isPresent()) {
            requirementTitle = isNotEmpty(narrative.get().title()) ? narrative.get().title() : requirementTitle;
            requirementType = isNotEmpty(narrative.get().type()) ? narrative.get().type() : requirementType;
            narrativeText = (narrative.get().text().length > 0) ? Joiner.on("\n").join(narrative.get().text()) : narrativeText;
            cardNumber = isNotEmpty(narrative.get().cardNumber()) ? narrative.get().cardNumber() : cardNumber;
        }
        if (isEmpty(requirementType)) {
            requirementType = getRequirementType(level, candidateClass);
        }

        return Requirement.named(humanReadableVersionOf(packageName))
                .withOptionalCardNumber(cardNumber)
                .withOptionalDisplayName(isEmpty(requirementTitle) ? humanReadableVersionOf(packageName) : humanReadableVersionOf(requirementTitle))
                .withType(requirementType)
                .withNarrative(narrativeText);
    }

    private String getRequirementType(int level, Class candidateClass) {
        if ((candidateClass != null) && (candidateClass.getName().endsWith(".package-info"))) {
            return getDefaultType(level);
        } else {
            return "story";
        }
    }

    private void persistRequirementsAsJSON(SortedMap<String, Requirement> requirementsByPath) {
        try {
            persister.write(requirementsByPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null || testOutcome.getUserStory().getStoryClassName() == null) {
            return Optional.absent();
        }
        String name = testOutcome.getUserStory().getStoryClassName().replace(rootPackage + ".", "");
        return Optional.fromNullable(getRequirementsByPath().get(name));
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        Optional<Requirement> result = Optional.absent();
        for (Requirement requirement : getRequirements()) {
            if (requirement.matchesTag(testTag)) {
                return Optional.of(requirement);
            }
        }
        return result;
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        Set<TestTag> result = new HashSet<>();
        for (Requirement requirement : getAllRequirements()) {
            if (isMatchingRequirementFor(testOutcome, requirement)) {
                result.add(requirement.asTag());
            }
        }
        return result;
    }

    protected List<Class<?>> loadClassesFromPath() {
        Set<Class<?>> classesWithNarratives = newHashSet(loadClasses().annotatedWith(Narrative.class)
                .fromPackage(rootPackage));

        Set<Class<?>> testCases = newHashSet(loadClasses().annotatedWith(RunWith.class)
                .fromPackage(rootPackage));

        Set<Class<?>> requirementClasses = newHashSet();
        requirementClasses.addAll(classesWithNarratives);
        requirementClasses.addAll(classesThatContainSerenityTestsIn(testCases));

        return ImmutableList.copyOf(requirementClasses);

    }

    private Set<? extends Class<?>> classesThatContainSerenityTestsIn(Set<Class<?>> testCases) {
        Set<Class<?>> matchingClasses = newHashSet();

        SerenityTestCaseFinder serenityTestCaseFinder = new SerenityTestCaseFinder();
        for (Class<?> testClass : testCases) {
            if (serenityTestCaseFinder.isSerenityTestCase(testClass)) {
                matchingClasses.add(testClass);
            }
        }
        return matchingClasses;
    }

    private static class RequirementChildLocator {
        Requirement parent;

        public RequirementChildLocator(Requirement parent) {
            this.parent = parent;
        }

        public List<Requirement> in(Collection<Requirement> requirements) {
            List<Requirement> children = Lists.newArrayList();
            for (Requirement requirement : requirements) {
                if (isNotEmpty(requirement.getParent()) && requirement.getParent().equals(parent.getName())) {
                    children.add(requirement);
                }
            }
            return children;
        }
    }


    private Collection<Requirement> getAllRequirements() {
        return getRequirementsByPath().values();
    }

    private boolean isMatchingRequirementFor(TestOutcome testOutcome, Requirement requirement) {
        if (testOutcome.getTestCase() != null) {
            return (fullPathOf(requirement).matchesOrIsADescendantOf(normalizedPath(testOutcome.getPathId())));
        } else {
            return (fullPathOf(requirement).matchesOrIsADescendantOf(normalizedPath(testOutcome.getPath())));
        }
    }

    private String normalizedPath(String path) {
        path = (path == null) ? "" : path.replaceAll("/", ".");
        for (String supportedSuffix : SUPPORTED_SUFFIXES) {
            if (path.endsWith("." + supportedSuffix)) {
                path = path.substring(0, path.lastIndexOf("." + supportedSuffix));
            }
        }
        if (!path.startsWith(rootPackage)) {
            path = rootPackage + "." + path;
        }
        return path;
    }


    private RequirementPathMatcher fullPathOf(Requirement requirement) {
        return new RequirementPathMatcher(requirement);
    }


    private class RequirementPathMatcher {
        String requirementPath;

        public RequirementPathMatcher(Requirement requirement) {
            requirementPath = rootPackage + "." + getRequirementPaths().get(requirement);
        }

        public boolean matchesOrIsADescendantOf(String path) {
            if (isNotEmpty(path)) {
                return path.startsWith(requirementPath) || requirementPath.startsWith(path);
            } else {
                return false;
            }
        }
    }
}
