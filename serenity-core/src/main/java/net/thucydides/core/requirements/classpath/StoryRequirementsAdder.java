package net.thucydides.core.requirements.classpath;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import net.thucydides.core.annotations.Narrative;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.annotations.NarrativeFinder;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Collection;
import java.util.List;

import static net.thucydides.core.requirements.classpath.PathElements.*;
import static net.thucydides.core.util.NameConverter.humanize;
import static org.apache.http.util.TextUtils.isEmpty;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.last;

public class StoryRequirementsAdder {

    private final String path;
    private final String rootPackage;

    RequirementsConfiguration requirementsConfiguration;

    public static StoryRequirementsAdderBuilder addStoryDefinedIn(String path) {
        return new StoryRequirementsAdderBuilder(path);
    }

    public static class StoryRequirementsAdderBuilder {

        private final String path;

        public StoryRequirementsAdderBuilder(String path) {

            this.path = path;
        }

        public StoryRequirementsAdder startingAt(String rootPackage) {
            return new StoryRequirementsAdder(path, rootPackage);
        }
    }

    protected StoryRequirementsAdder(String path, String rootPackage) {
        this.path = path;
        this.rootPackage = rootPackage;
        this.requirementsConfiguration = new RequirementsConfiguration(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public Requirement to(Collection<Requirement> allRequirements) {
        List<String> pathElements = elementsOf(path, rootPackage);
        String storyName = humanize(lastOf(pathElements));

        String parent = null;
        if (pathElements.size() >= 2) {
            parent = humanize(secondLastOf(pathElements));
        }

        Requirement story = Requirement.named(storyName)
                .withType(narrativeTypeDefinedIn(path).or(leafRequirementType()))
                .withNarrative(narrativeTextDefinedIn(path).or(""))
                .withParent(parent);

        allRequirements.add(story);

        return story;
    }

    private Optional<String> narrativeTypeDefinedIn(String path) {
        Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
        if (!narrative.isPresent() || (isEmpty(narrative.get().type()))) {
            return Optional.absent();
        }
        return Optional.of(narrative.get().type());
    }

    private Optional<String> narrativeTextDefinedIn(String path) {
        Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
        if (!narrative.isPresent()) {
            return Optional.absent();
        }
        String narrativeText = Joiner.on("\n").join(narrative.get().text());
        return Optional.of((narrative.get().title() + System.lineSeparator() + narrativeText).trim());
    }

    public Optional<Narrative> getClassLevelNarrativeFor(String path) {
        try {
            return NarrativeFinder.forClass(getClass().getClassLoader().loadClass(path));
        } catch (ClassNotFoundException e) {
            return Optional.absent();
        }
    }

    private String leafRequirementType() {
        return last(requirementsConfiguration.getRequirementTypes());
    }
}