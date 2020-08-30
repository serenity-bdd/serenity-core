package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import com.google.common.reflect.ClassPath;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.annotations.ClassInfoAnnotations;
import net.thucydides.core.requirements.classpath.LeafRequirementAdder;
import net.thucydides.core.requirements.classpath.NonLeafRequirementsAdder;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 * By default, the tests
 */
public class PackageRequirementsTagProvider extends AbstractRequirementsTagProvider
        implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {

    private final EnvironmentVariables environmentVariables;

    private final String rootPackage;

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
        this(environmentVariables, ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables));
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

        if (rootPackage == null) {
            return NO_REQUIREMENTS;
        }

        if (requirements == null) {
            fetchRequirements();
        }

        return requirements;
    }

    private void fetchRequirements() {
        logger.debug("Loading requirements from package requirements at: " + rootPackage);

        requirements = reloadedRequirements().orElse(requirementsReadFromClasspath()
                .orElse(NO_REQUIREMENTS));
    }

    private java.util.Optional<List<Requirement>> reloadedRequirements() {
        try {
            return requirementsStore.read();
        } catch (IOException e) {
            return java.util.Optional.empty();
        }
    }

    private Optional<List<Requirement>> requirementsReadFromClasspath() {

        List<Requirement> classpathRequirements = null;

        try {
            List<String> requirementPaths = requirementPathsStartingFrom(rootPackage);
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

    private final Lock readingPaths = new ReentrantLock();

    private List<String> requirementPathsStartingFrom(String rootPackage) {

        if (isEmpty(rootPackage)) {
            return new ArrayList<>();
        }

        if (requirementPaths == null) {
            readingPaths.lock();
            List<String> paths = requirementPathsFromClassesInPackage(rootPackage);
            Collections.sort(paths, byDescendingPackageLength());
            requirementPaths = NewList.copyOf(paths);
            readingPaths.unlock();
        }
        return requirementPaths;
    }

    private List<String> requirementPathsFromClassesInPackage(String rootPackage) {
        try {
            ClassPath classpath = ClassPath.from(Thread.currentThread().getContextClassLoader());
            return classpath.getTopLevelClassesRecursive(rootPackage)
                    .stream()
                    .filter(classInfo -> classRepresentsARequirementIn(classInfo))
                    .map(classInfo -> classInfo.getName())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new CouldNotLoadRequirementsException(e);
        }
    }

    private boolean classRepresentsARequirementIn(ClassPath.ClassInfo classInfo) {
        return (ClassInfoAnnotations.theClassDefinedIn(classInfo)
                .hasAnAnnotation(net.thucydides.core.annotations.Narrative.class))
                || (ClassInfoAnnotations.theClassDefinedIn(classInfo)
                .hasAPackageAnnotation(net.thucydides.core.annotations.Narrative.class))
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
            String pathWithoutRootPackage = path.replace(rootPackage + ".", "");
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
                .startingAt(rootPackage)
                .to(allRequirements);

        NonLeafRequirementsAdder.addParentsOf(leafRequirement)
                .in(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
                .startingAt(rootPackage)
                .to(allRequirements);

    }

    @Override
    public java.util.Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        return getTestCaseRequirementOf(testOutcome);
    }

    public java.util.Optional<Requirement> getTestCaseRequirementOf(TestOutcome testOutcome) {
//        for (Requirement requirement : AllRequirements.in(getRequirements())) {
//            if (requirement.asTag().isAsOrMoreSpecificThan(testOutcome.getUserStory().asTag())) {
//                return java.util.Optional.of(requirement);
//            }
//        }
//        return java.util.Optional.empty();
//
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
//
//        for (Requirement requirement : AllRequirements.in(getRequirements())) {
//            if (requirement.asTag().isAsOrMoreSpecificThan(testTag)) {
//                return Optional.of(requirement);
//            }
//        }
        return uniqueRequirementWithName(testTag.getName());
    }

    private java.util.Optional<Requirement> uniqueRequirementWithName(String name) {
        return RequirementsList.of(getRequirements()).findByUniqueName(name);
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null) {
            return new HashSet();
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

        int maxDepth = longestPathIn(requirementPathsStartingFrom(rootPackage));// RequirementsList.of(getRequirements()).maxDepth();

        return applicableRequirements(allRequirementTypes, maxDepth);
    }

    private List<String> applicableRequirements(List<String> allRequirementTypes, int maxDepth) {
        int startingLevel = max(allRequirementTypes.size() - maxDepth, 0);
        int endingLevel = allRequirementTypes.size();
        return allRequirementTypes.subList(startingLevel, endingLevel);
    }
}
