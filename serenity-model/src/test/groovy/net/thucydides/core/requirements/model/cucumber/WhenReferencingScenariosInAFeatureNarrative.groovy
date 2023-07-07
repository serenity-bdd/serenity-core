package net.thucydides.core.requirements.model.cucumber

//import io.cucumber.core.gherkin.messages.internal.gherkin.Gherkin
import io.cucumber.messages.IdGenerator
import io.cucumber.messages.types.Envelope
import io.cucumber.messages.types.Feature
import spock.lang.Specification

import java.util.stream.Collectors

class WhenReferencingScenariosInAFeatureNarrative extends Specification {

    def featureFile = "src/test/resources/todomvc/features/maintain_my_todo_list/filtering_todos.feature"
    def features = loadCucumberFeatures([featureFile])
    def filteringTodoFeature = features[0]

    private List<Feature> loadCucumberFeatures(List<String> listOfFiles) {
        CucumberParser parser = new CucumberParser()
        ArrayList<Feature> features = new ArrayList<>();
        for(String currentFile: listOfFiles) {
            Optional<AnnotatedFeature> feature = parser.loadFeature(new File(currentFile))
            features.add(feature.get().feature);
        }
        return features;

    }
    def "Should be able to identify scenarios in a feature file by name"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("View only the completed items").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("Given that Jane has a todo list containing Buy some milk, Walk the dog  ")
    }

    def "Should report scenario outline given-when-then statements with variables"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("Viewing the items by status").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("When she filters her list to show only {filter} tasks")
    }

    def "Should indicate if a scenario is not known"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("Unknown scenario").asGivenWhenThen()
        then:
        !scenarioDescription.isPresent()
    }

    def "Should not return the examples table if none are present"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("View only completed items").asExampleTable()
        then:
        !scenarioDescription.isPresent()
    }

    def "Should not change lines with no scenario references"() {
        expect:
        DescriptionWithScenarioReferences.from(filteringTodoFeature).forText("No scenario reference") == "No scenario reference"
    }

    def "should render narratives with example tables"() {
        given:
        CucumberParser parser = new CucumberParser()
        when:
            def narrative = parser.loadFeatureDefinition(new File(featureFile))
        then:
            narrative.isPresent()
    }

    def normalized(String text) {
        return text.replaceAll("\\n","").replaceAll("\\r","")
    }

}
