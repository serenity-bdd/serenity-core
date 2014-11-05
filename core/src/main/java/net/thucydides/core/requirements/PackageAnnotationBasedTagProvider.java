package net.thucydides.core.requirements;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.core.requirements.annotations.NarrativeFinder;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT;

/**
 * A requirements Provider that reads requirement from class or package annotation.
 * A class or package needs to be annotated with {@link net.thucydides.core.annotations.Narrative}
 * to be a requirement. All package above the class or package will also be considered requirement.
 * The root package is defined using {@link net.thucydides.core.ThucydidesSystemProperty#THUCYDIDES_TEST_ROOT}
 * It is recommended to change the root package if the {@link FileSystemRequirementsTagProvider} is used.
 *
 * @see net.thucydides.core.annotations.Narrative
 * @see net.thucydides.core.ThucydidesSystemProperty#THUCYDIDES_TEST_ROOT
 */
public class PackageAnnotationBasedTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider {
    private static final String DOT_REGEX = "\\.";
    private final static List<String> SUPPORTED_SUFFIXES = ImmutableList.of("story","feature");

    private List<Requirement> requirements;
    private final Configuration configuration;
    private final RequirementPersister persister;
    private final String rootPackage;

    private List<Requirement> leafRequirements;
    SortedMap<String, Requirement> requirementsByPath = Maps.newTreeMap();
    Map<Requirement, String> requirementPaths = Maps.newHashMap();

    public PackageAnnotationBasedTagProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public PackageAnnotationBasedTagProvider(EnvironmentVariables vars) {
        super(vars);
        configuration = new SystemPropertiesConfiguration(environmentVariables);
        rootPackage = THUCYDIDES_TEST_ROOT.from(environmentVariables, rootDirectory);
        persister = new RequirementPersister(configuration.getOutputDirectory(), rootPackage);
        leafRequirements = Lists.newArrayList();
    }

    private Collection<Requirement> getAllRequirements() {
        return getRequirementsByPath().values();
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        Set<TestTag> result = new HashSet<>();
        for (Requirement requirement : getAllRequirements()) {
            if (isMatchingRequirementFor(testOutcome, requirement)) {
                result.add(TestTag.withName(humanReadableVersionOf(requirement.getName())).andType(requirement.getType()));
            }
        }
        return result;
    }

    private boolean isMatchingRequirementFor(TestOutcome testOutcome, Requirement requirement) {
        return (fullPathOf(requirement).matchesOrIsADescendantOf(normalizedPath(testOutcome.getPathId())))
                || (fullPathOf(requirement).matchesOrIsADescendantOf(normalizedPath(testOutcome.getPath())));
    }

    private String normalizedPath(String path) {
        path = path.replaceAll("/",".");
        for(String supportedSuffix: SUPPORTED_SUFFIXES) {
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

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = loadRequirements();
        }
        return requirements;
    }

    private List<Requirement> loadRequirements() {
        List<Class<?>> classes = loadClasses();

        if (classes.isEmpty()) {
            requirementsByPath = loadFromJSON();
        } else {
            loadRequirementsFromClasses(classes);
        }
        requirementPaths = indexRequirements(requirementsByPath);
        List<Requirement> requirementsTree = buildRequirementsTree(requirementsByPath, requirementPaths);
        return ImmutableList.copyOf(requirementsTree);
    }

    protected List<Class<?>> loadClasses() {
        return ClassFinder.loadClasses().annotatedWith(Narrative.class).fromPackage(rootPackage);
    }

    private SortedMap<String, Requirement> loadFromJSON() {
        try {
            return persister.read();
        } catch (IOException e) {
            e.printStackTrace();
            return new TreeMap<>();
        }
    }

    private Map<Requirement, String> indexRequirements(SortedMap<String, Requirement> requirementsByPath) {
        Map<Requirement, String> requirementPaths = Maps.newHashMap();
        for(String path : requirementsByPath.keySet()) {
            Requirement requirement = requirementsByPath.get(path);
            requirementPaths.put(requirement, path);

        }
        return requirementPaths;
    }

    private void loadRequirementsFromClasses(List<Class<?>> classes) {
        for (Class candidateClass : classes) {
            addRequirementTo(requirementsByPath, candidateClass);
        }
        leafRequirements = findLeafRequirementsIn(requirementsByPath);
        persistRequirementsAsJSON(requirementsByPath);
    }

    private void persistRequirementsAsJSON(SortedMap<String, Requirement> requirementsByPath) {
        try {
            persister.write(requirementsByPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Requirement> buildRequirementsTree(SortedMap<String, Requirement> requirementsByPath,
                                                    Map<Requirement, String> requirementPaths) {
        List<Requirement> requirementsTree = Lists.newArrayList();
        for (Requirement requirement : requirementsByPath.values()) {
            if (isRoot(requirementPaths.get(requirement))) {
                List<Requirement> children = findDirectChildrenFor(requirement, requirementsByPath, requirementPaths);
                requirementsTree.add(requirement.withChildren(children));
            }
        }
        return requirementsTree;
    }

    private boolean isRoot(String path) {
        return !path.contains(".");
    }

    private List<Requirement> findDirectChildrenFor(Requirement requirement,
                                                    SortedMap<String, Requirement> requirementsByPath,
                                                    Map<Requirement, String> requirementPaths) {

        List<Requirement> immediateChildren = Lists.newArrayList();
        if (!isLeaf(requirement)) {
            String requirementPath = requirementPaths.get(requirement);
            for (String path : requirementsByPath.keySet()) {
                Requirement childRequirement = requirementsByPath.get(path);

                if ((childRequirement != requirement) && (isImmediateChild(requirementPath, path))) {
                    if (isLeaf(childRequirement)) {
                        immediateChildren.add(childRequirement);
                    } else {
                        immediateChildren.add(childRequirement.withChildren(findDirectChildrenFor(childRequirement,
                                requirementsByPath,
                                requirementPaths)));
                    }
                }
            }
        }
        return immediateChildren;
    }

    private boolean isLeaf(Requirement childRequirement) {
        return leafRequirements.contains(childRequirement);
    }

    private boolean isImmediateChild(String requirementPath, String path) {
        if (path.startsWith(requirementPath)) {
            String trailingPath = path.replaceFirst(requirementPath + ".", "");
            return (!StringUtils.isEmpty(trailingPath) && !trailingPath.contains("."));
        } else {
            return false;
        }
    }

    private String getFullRequirementPath(Class candidateClass) {
        return candidateClass.getName().replace(rootPackage + ".", "").replace(".package-info", "");
    }

    private void addRequirementTo(SortedMap<String, Requirement> requirementsByPath,
                                  Class candidateClass) {

        String fullRequirementName = getFullRequirementPath(candidateClass);


        String[] packageNames = fullRequirementName.split(DOT_REGEX);
        String currentPath = "";
        for (int level = 0; level < packageNames.length; level++) {
            currentPath = (currentPath.isEmpty()) ? packageNames[level] : Joiner.on(".").join(currentPath, packageNames[level]);
            String defaultRequirementType = getDefaultType(level);
            Requirement currentRequirement;
            if (!requirementsByPath.containsKey(currentPath)) {
                if (level < packageNames.length - 1) {
                    currentRequirement = newParentRequirement(currentPath, packageNames[level], level, defaultRequirementType);
                    requirementsByPath.put(currentPath, currentRequirement);
                } else {
                    currentRequirement = newRequirement(candidateClass, currentPath, packageNames[level], level, defaultRequirementType);
                    String fullPath = getFullRequirementPath(candidateClass);
                    requirementsByPath.put(fullPath, currentRequirement);
                }
            }
        }
    }

    private List<Requirement> findLeafRequirementsIn(Map<String, Requirement> requirementsByPath) {
        List<Requirement> leafRequirements = Lists.newArrayList();
        for (String path : requirementsByPath.keySet()) {
            if (!longerPathExists(path, requirementsByPath.keySet())) {
                leafRequirements.add(requirementsByPath.get(path));
            }
        }
        return leafRequirements;
    }

    private boolean longerPathExists(String path, Set<String> paths) {
        for (String requirementPath : paths) {
            if (requirementPath.startsWith(path) && (requirementPath.length() > path.length())) {
                return true;
            }
        }
        return false;
    }

    private Requirement newParentRequirement(String requirementPath,
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

        return getRequirement(candidateClass, packageName, level, requirementTitle, requirementType, narrativeText, cardNumber, narrative);
    }

    private Requirement newRequirement(Class candidateClass,
                                       String currentPath,
                                       String packageName,
                                       int level,
                                       String defaultRequirementType) {
        String requirementTitle = packageName;
        String requirementType = defaultRequirementType;
        String narrativeText = "";
        String cardNumber = "";

        Optional<Narrative> narrative = NarrativeFinder.forClass(candidateClass);

        return getRequirement(candidateClass, packageName, level, requirementTitle, requirementType, narrativeText, cardNumber, narrative);
    }

    private Requirement getRequirement(Class candidateClass, String packageName, int level, String requirementTitle, String requirementType, String narrativeText, String cardNumber, Optional<Narrative> narrative) {
        if (narrative.isPresent()) {
            requirementTitle = narrative.get().title();
            requirementType = narrative.get().type();
            narrativeText = Joiner.on("\n").join(narrative.get().text());
            cardNumber = narrative.get().cardNumber();
        }
        if (StringUtils.isEmpty(requirementType)) {
            requirementType = getRequirementType(level, candidateClass);
        }

        return Requirement.named(humanReadableVersionOf(packageName))
                .withOptionalCardNumber(cardNumber)
                .withOptionalDisplayName(StringUtils.isEmpty(requirementTitle) ? humanReadableVersionOf(packageName) : requirementTitle)
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

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null
                || testOutcome.getUserStory().getStoryClassName() == null) {
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

    public SortedMap<String, Requirement> getRequirementsByPath() {
        getRequirements();
        return requirementsByPath;
    }

    private class RequirementPathMatcher {
        String requirementPath;

        public RequirementPathMatcher(Requirement requirement) {
            requirementPath = rootPackage + "." + requirementPaths.get(requirement);
        }

        public boolean matchesOrIsADescendantOf(String path) {
            if (StringUtils.isNotEmpty(path)) {
                return path.startsWith(requirementPath) || requirementPath.startsWith(path);
            } else {
                return false;
            }
        }
    }
}
