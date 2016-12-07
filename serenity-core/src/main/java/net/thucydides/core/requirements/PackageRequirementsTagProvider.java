package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.thucydides.core.requirements.annotations.ClassInfoAnnotations.theClassDefinedIn;
import static net.thucydides.core.requirements.classpath.LeafRequirementAdder.addLeafRequirementDefinedIn;
import static net.thucydides.core.requirements.classpath.NonLeafRequirementsAdder.addParentsOf;

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

    private final List<Requirement> NO_REQUIREMENTS = Lists.newArrayList();

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
        requirements = reloadedRequirements()
                            .or(requirementsReadFromClasspath()
                                    .or(NO_REQUIREMENTS));
    }

    private Optional<List<Requirement>> reloadedRequirements() {
        try {
            return requirementsStore.read();
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private Optional<List<Requirement>> requirementsReadFromClasspath() {

        List<Requirement> classpathRequirements = null;

        try {
            List<String> requirementPaths = requirementPathsStartingFrom(rootPackage);
            Collections.sort(requirementPaths, byDescendingPackageLength());
//            int requirementsDepth = shortestPathIn(requirementPaths);
            int requirementsDepth = longestPathIn(requirementPaths);

            Set<Requirement> allRequirements = Sets.newHashSet();
            for (String path : requirementPaths) {
                addRequirementsDefinedIn(path, requirementsDepth, allRequirements);
            }

            allRequirements = removeChildrenFromTopLevelRequirementsIn(allRequirements);

            if (!allRequirements.isEmpty()) {
                classpathRequirements = Lists.newArrayList(allRequirements);
                Collections.sort(classpathRequirements);

                requirementsStore.write(classpathRequirements);
            }

        } catch (IOException e) {
            return Optional.absent();
        }

        return Optional.fromNullable(classpathRequirements);
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

    List<String> requirementPaths;
    private List<String> requirementPathsStartingFrom(String rootPackage){
        if (requirementPaths == null) {
            requirementPaths = requirementPathsFromClassesInPackage(rootPackage);
        }
        return requirementPaths;
    }

    private List<String> requirementPathsFromClassesInPackage(String rootPackage) {
        List<String> requirementPaths = Lists.newArrayList();

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
        return (theClassDefinedIn(classInfo).hasAnAnnotation(RunWith.class, Narrative.class))
                || (theClassDefinedIn(classInfo).hasAPackageAnnotation(Narrative.class))
                || (theClassDefinedIn(classInfo).containsTests());
    }


    private Set<Requirement> removeChildrenFromTopLevelRequirementsIn(Set<Requirement> allRequirements) {
        Set<Requirement> prunedRequirements = Sets.newHashSet();
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

        Requirement leafRequirement = addLeafRequirementDefinedIn(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
                .usingRequirementTypes(getActiveRequirementTypes())
                .startingAt(rootPackage)
                .to(allRequirements);

        addParentsOf(leafRequirement)
                .in(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
                .startingAt(rootPackage)
                .to(allRequirements);

    }


    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        return getTestCaseRequirementOf(testOutcome);
    }

    public Optional<Requirement> getTestCaseRequirementOf(TestOutcome testOutcome) {
        for (Requirement requirement : AllRequirements.in(getRequirements())) {
            if (requirement.asTag().isAsOrMoreSpecificThan(testOutcome.getUserStory().asTag())) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        for (Requirement requirement : AllRequirements.in(getRequirements())) {
            if (requirement.asTag().isAsOrMoreSpecificThan(testTag)) {
                return Optional.of(requirement);
            }
        }
        return uniqueRequirementWithName(testTag.getName());
    }

    private Optional<Requirement> uniqueRequirementWithName(String name) {
        return RequirementsList.of(getRequirements()).findByUniqueName(name);
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        if (testOutcome.getUserStory() == null) {
            return Sets.newHashSet();
        }

        Set<TestTag> tags = new HashSet<>();

        Optional<Requirement> matchingRequirement = getRequirementFor(testOutcome.getUserStory().asTag());

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
        return Optional.absent();
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
