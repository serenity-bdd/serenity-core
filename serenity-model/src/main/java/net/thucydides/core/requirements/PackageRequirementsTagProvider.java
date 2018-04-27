package net.thucydides.core.requirements;

import com.google.common.base.*;
import com.google.common.reflect.*;
import net.serenitybdd.core.collect.*;
import net.serenitybdd.core.environment.*;
import net.thucydides.core.*;
import net.thucydides.core.model.*;
import net.thucydides.core.requirements.annotations.*;
import net.thucydides.core.requirements.classpath.*;
import net.thucydides.core.requirements.model.*;
import net.thucydides.core.util.*;
import org.junit.runner.*;

import java.io.*;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.locks.*;

import static java.lang.Math.*;

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

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables,
                                          String rootPackage,
                                          RequirementsStore requirementsStore) {
        super(environmentVariables);
        this.environmentVariables = environmentVariables;
        this.rootPackage = rootPackage;
        this.requirementsStore = requirementsStore;
    }

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables, String rootPackage) {
        this(environmentVariables, rootPackage,
                new FileSystemRequirementsStore(getRequirementsDirectory(ConfiguredEnvironment.getConfiguration().getOutputDirectory()),
                                                rootPackage + "-package-requirements.json"));
    }

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this(environmentVariables, ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables));
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

            Set<Requirement> allRequirements = new HashSet();
            for (String path : requirementPaths) {
                addRequirementsDefinedIn(path, requirementsDepth, allRequirements);
            }

            allRequirements = removeChildrenFromTopLevelRequirementsIn(allRequirements);

            if (!allRequirements.isEmpty()) {
                classpathRequirements =new ArrayList<>(allRequirements);
                Collections.sort(classpathRequirements);

                requirementsStore.write(classpathRequirements);
            }

        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(classpathRequirements);
    }

    private Comparator<? super String> byDescendingPackageLength() {
        return new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer o1Length = Splitter.on(".").splitToList(o1).size();
                Integer o2Length = Splitter.on(".").splitToList(o2).size();
                return o1Length.compareTo(o2Length);
            }
        };
    }

    private final Lock readingPaths = new ReentrantLock();

    private List<String> requirementPathsStartingFrom(String rootPackage){

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
        List<String> requirementPaths = new ArrayList<>();

        ClassPath classpath;
        try {
            classpath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        } catch (IOException e) {
            throw new CouldNotLoadRequirementsException(e);
        }

        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(rootPackage)) {
            if (classRepresentsARequirementIn(classInfo)) {
                requirementPaths.add(classInfo.getName());
            }
        }
        return requirementPaths;
    }

    private boolean classRepresentsARequirementIn(ClassPath.ClassInfo classInfo) {
        return (ClassInfoAnnotations.theClassDefinedIn(classInfo).hasAnAnnotation(RunWith.class, net.thucydides.core.annotations.Narrative.class))
                || (ClassInfoAnnotations.theClassDefinedIn(classInfo).hasAPackageAnnotation(net.thucydides.core.annotations.Narrative.class))
                || (ClassInfoAnnotations.theClassDefinedIn(classInfo).containsTests());
    }


    private Set<Requirement> removeChildrenFromTopLevelRequirementsIn(Set<Requirement> allRequirements) {
        Set<Requirement> prunedRequirements = new HashSet();
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
        for (Requirement requirement : AllRequirements.in(getRequirements())) {
            if (requirement.asTag().isAsOrMoreSpecificThan(testOutcome.getUserStory().asTag())) {
                return java.util.Optional.of(requirement);
            }
        }
        return java.util.Optional.empty();
    }

    @Override
    public java.util.Optional<Requirement> getRequirementFor(TestTag testTag) {
        for (Requirement requirement : AllRequirements.in(getRequirements())) {
            if (requirement.asTag().isAsOrMoreSpecificThan(testTag)) {
                return Optional.of(requirement);
            }
        }
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
        for (Requirement requirement : AllRequirements.in(requirements)) {
            if (requirement.getChildren().contains(child)) {
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
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
