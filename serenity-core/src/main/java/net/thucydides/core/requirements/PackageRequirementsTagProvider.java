package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.thucydides.core.requirements.classpath.NonLeafRequirementsAdder.addParentsOf;
import static net.thucydides.core.requirements.classpath.StoryRequirementsAdder.addStoryDefinedIn;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 * By default, the tests
 */
public class PackageRequirementsTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider {

    private final EnvironmentVariables environmentVariables;

    private final String rootPackage;

    private List<Requirement> requirements;

    private final Configuration configuration;

    private final RequirementsStore requirementsStore;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables, String rootPackage) {
        super(environmentVariables);
        this.environmentVariables = environmentVariables;
        this.rootPackage = rootPackage;
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
        this.requirementsStore = new RequirementsStore(getRequirementsDirectory(), "package-requirements.json");

        if (rootPackage == null) {
            logger.warn("To generate correct requirements coverage reports you need to set the 'serenity.test.root' property to the package representing the top of your requirements hierarchy.");
        }
    }

    public PackageRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this(environmentVariables, ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables));
    }

    public PackageRequirementsTagProvider() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    private final List<Requirement> NO_REQUIREMENTS = Lists.newArrayList();

    public void clear() {
        requirementsStore.clear();
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
        Optional<List<Requirement>> reloadedRequirements = reloadedRequirements();
        requirements =  reloadedRequirements.isPresent() ? reloadedRequirements.get() : requirementsReadFromClasspath();
    }

    private Optional<List<Requirement>> reloadedRequirements() {
        try {
            return requirementsStore.read();
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private List<Requirement> requirementsReadFromClasspath() {
        try {

            List<String> requirementPaths = requirementPathsStartingFrom(rootPackage);
            int requirementsDepth = longestPathIn(requirementPaths);

            Set<Requirement> allRequirements = Sets.newHashSet();
            for (String path : requirementPaths) {
                addRequirementsDefinedIn(path, requirementsDepth, allRequirements);
            }

            allRequirements = removeChildrenFromTopLevelRequirementsIn(allRequirements);

            requirements = Lists.newArrayList(allRequirements);
            Collections.sort(requirements);

            requirementsStore.write(requirements);


        } catch (IOException e) {
            requirements = new ArrayList<>();
        }
        return requirements;
    }

    private List<String> requirementPathsStartingFrom(String rootPackage) throws IOException {
        List<String> requirementPaths = requirementPathsFromClassesInPackage(rootPackage);
        return requirementPaths;
    }

    private List<String> requirementPathsFromClassesInPackage(String rootPackage) throws IOException {
        List<String> requirementPaths = Lists.newArrayList();
        ClassPath classpath = ClassPath.from(Thread.currentThread().getContextClassLoader());

        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(rootPackage)) {
            if (classRepresentsARequirementIn(classInfo)) {
                requirementPaths.add(classInfo.getName());
            }
        }
        return requirementPaths;
    }

    private boolean classRepresentsARequirementIn(ClassPath.ClassInfo classInfo) {
        if ((classInfo.load().getAnnotation(RunWith.class) != null)) {
            return true;
        }
        if (classInfo.load().getPackage().getAnnotation(Narrative.class) != null) {
            return true;
        }
        return false;
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
        return maxDepth;
    }

    private void addRequirementsDefinedIn(String path, int requirementsDepth, Collection<Requirement> allRequirements) {

        Requirement leafRequirement = addStoryDefinedIn(path)
                .withAMaximumRequirementsDepthOf(requirementsDepth)
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
        return Optional.absent();
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

    private File getRequirementsDirectory() {
        return new File(configuration.getOutputDirectory(),"requirements");
    }

}
