package net.thucydides.core.requirements.classpath;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Collection;
import java.util.List;

import static net.thucydides.core.requirements.classpath.PathElements.*;
import static net.thucydides.core.util.NameConverter.humanize;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.last;

public class StoryRequirementsAdder {

    private final String path;
    private final String rootPackage;
    private final int requirementsDepth;

    RequirementsConfiguration requirementsConfiguration;

    public static StoryRequirementsAdderBuilder addStoryDefinedIn(String path) {
        return new StoryRequirementsAdderBuilder(path);
    }

    public static class StoryRequirementsAdderBuilder {

        private final String path;
        private int requirementsDepth;

        public StoryRequirementsAdderBuilder(String path) {

            this.path = path;
        }

        public StoryRequirementsAdder startingAt(String rootPackage) {
            return new StoryRequirementsAdder(path, requirementsDepth, rootPackage);
        }

        public StoryRequirementsAdderBuilder withAMaximumRequirementsDepthOf(int requirementsDepth) {
            this.requirementsDepth = requirementsDepth;
            return this;
        }

    }

    protected StoryRequirementsAdder(String path, int requirementsDepth, String rootPackage) {
        this.path = path;
        this.rootPackage = rootPackage;
        this.requirementsDepth = requirementsDepth;
        this.requirementsConfiguration = new RequirementsConfiguration(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }


    public Requirement to(Collection<Requirement> allRequirements) {

        Requirement newRequirement = PackageInfoClass.isDefinedIn(path) ?
                requirementDefinedByNarrativeAt(path) : requirementDefinedByClassNameAt(path);

        allRequirements.add(newRequirement);

        return newRequirement;
    }

    private Requirement requirementDefinedByClassNameAt(String path) {

        List<String> pathElements = elementsOf(path, rootPackage);
        String storyName = humanize(lastOf(pathElements));

        String parent = null;
        if (pathElements.size() >= 2) {
            parent = humanize(secondLastOf(pathElements));
        }

        String narrativeText = PackageInfoNarrative.text().definedInPath(path)
                               .or(ClassNarrative.text().definedInPath(path))
                               .or("");

        String narrativeType = PackageInfoNarrative.type().definedInPath(path)
                .or(ClassNarrative.type().definedInPath(path))
                .or(leafRequirementType());

        Requirement story = Requirement.named(storyName)
                .withType(narrativeType)
                .withNarrative(narrativeText)
                .withParent(parent);

        return story;
    }

    private Requirement requirementDefinedByNarrativeAt(String path) {

        List<String> pathElements = elementsOf(path, rootPackage);
        List<String> featurePathElements = allButLast(pathElements);
        String featureName = humanize(lastOf(featurePathElements));

        String parent = null;
        if (featurePathElements.size() >= 2) {
            parent = humanize(secondLastOf(featurePathElements));
        }

        int startFromRequirementLevel = requirementsConfiguration.getRequirementTypes().size() - requirementsDepth;
        String typeByLevel = requirementsConfiguration.getRequirementType(startFromRequirementLevel + featurePathElements.size() - 1);


        Requirement story = Requirement.named(featureName)
                .withType(PackageInfoNarrative.type().definedInPath(path).or(typeByLevel))
                .withNarrative(PackageInfoNarrative.text().definedInPath(path).or(""))
                .withParent(parent);

        return story;
    }


//    private Optional<String> narrativeTypeDefinedIn(String path) {
//        Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
//        if (!narrative.isPresent() || (isEmpty(narrative.get().type()))) {
//            return Optional.absent();
//        }
//        return Optional.of(narrative.get().type());
//    }
//
//    private Optional<String> narrativeTextDefinedIn(String path) {
//        Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
//        if (!narrative.isPresent()) {
//            return Optional.absent();
//        }
//        String narrativeText = Joiner.on("\n").join(narrative.get().text());
//        return Optional.of((narrative.get().title() + System.lineSeparator() + narrativeText).trim());
//    }
//
//    public Optional<Narrative> getClassLevelNarrativeFor(String path) {
//        try {
//            return NarrativeFinder.forClass(getClass().getClassLoader().loadClass(path));
//        } catch (ClassNotFoundException e) {
//            return Optional.absent();
//        }
//    }

    private String leafRequirementType() {
        return last(requirementsConfiguration.getRequirementTypes());
    }
}