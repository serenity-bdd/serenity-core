package net.thucydides.core.requirements.model.cucumber

import io.cucumber.core.gherkin.messages.internal.gherkin.Gherkin
import io.cucumber.messages.IdGenerator
import io.cucumber.messages.types.Envelope
import io.cucumber.messages.types.Feature
import spock.lang.Specification

import java.util.stream.Collectors

class WhenReferencingScenariosInAFeatureNarrative extends Specification {

    def featureFile = "src/test/resources/serenity-cucumber/features/maintain_my_todo_list/filtering_todos.feature"
    def features = loadCucumberFeatures([featureFile])
    def filteringTodoFeature = features[0]

    private List<Feature> loadCucumberFeatures(List<String> listOfFiles) {
        IdGenerator idGenerator = new IdGenerator.Incrementing();
        List<Feature> loadedFeatures = new ArrayList<>();
        boolean includeSource = false;
        boolean includeAst = true;
        boolean includePickles = false;
        List<Envelope> envelopes = Gherkin.fromPaths(listOfFiles, includeSource, includeAst, includePickles, idGenerator).collect(Collectors.toList());
        for(Envelope envelope : envelopes )
        {
            if(envelope.gherkinDocument && envelope.gherkinDocument.feature)
            {
                loadedFeatures.add(envelope.getGherkinDocument().getFeature());
            }
        }
        return loadedFeatures;
    }
    def "Should be able to identify scenarios in a feature file by name"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("View only completed items").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("Given that Jane has a todo list containing Buy some milk, Walk the dog  ")
        scenarioDescription.get().contains("And she has completed the task called 'Walk the dog'  ")
        scenarioDescription.get().contains("When she filters her list to show only Completed tasks  ")
        scenarioDescription.get().contains("Then her todo list should contain Walk the dog  ")
    }

    def "Should report scenario outline given-when-then statements with variables"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("Do many things").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("Given that Jane has a todo list containing {tasks}  ")
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


    def "Should return the examples table alone for scenario outline if requested"() {
        when:
        def examples = ReferencedScenario.in(filteringTodoFeature).withName("Do many things").asExampleTable(ScenarioDisplayOption.WithTitle)
        then:
        examples.isPresent()
        and:
        normalized(examples.get()) == normalized("""### Do some things

| tasks                       | filter    | expected      |&nbsp;|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do[31]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do[32]}|

[<i class="fa fa-info-circle"></i> More details](#2d2f4c1a070b3434226e9ee8cf6f2d75c0e2847d548719e5ba07a47c67c1030a)
### Do some other things

| tasks                       | filter    | expected      |&nbsp;|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do[36]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do[37]}|

[<i class="fa fa-info-circle"></i> More details](#2d2f4c1a070b3434226e9ee8cf6f2d75c0e2847d548719e5ba07a47c67c1030a)
""")
    }


    def "Should not change lines with no scenario references"() {
        expect:
        DescriptionWithScenarioReferences.from(filteringTodoFeature).forText("No scenario reference") == "No scenario reference"
    }

    def "Should replace scenario references with the Given-When-Then text"() {
        expect:
        normalized(DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("{Scenario} View only completed items")) == normalized("""Given that Jane has a todo list containing Buy some milk, Walk the dog    
And she has completed the task called 'Walk the dog'    
When she filters her list to show only Completed tasks    
Then her todo list should contain Walk the dog    {result:Filtering things I need to do!View only completed items}
[<i class="fa fa-info-circle"></i> More details](#a043407804870e3b974cd3d28b836eb49b21f28593eca8fa1807cdfa3863ed1e)
""")

    }

    def "Should ignore leading spaces"() {
        expect:
        normalized(DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("  {Scenario}   View only completed items")) == normalized("""Given that Jane has a todo list containing Buy some milk, Walk the dog    
And she has completed the task called 'Walk the dog'    
When she filters her list to show only Completed tasks    
Then her todo list should contain Walk the dog    {result:Filtering things I need to do!View only completed items}
[<i class="fa fa-info-circle"></i> More details](#a043407804870e3b974cd3d28b836eb49b21f28593eca8fa1807cdfa3863ed1e)
""")

    }

    def "Should replace example references with the example table"() {
        expect:
        normalized(DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("{Examples} Do many things")) ==  normalized("""
| tasks                       | filter    | expected      |&nbsp;|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do[31]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do[32]}|

[<i class="fa fa-info-circle"></i> More details](#2d2f4c1a070b3434226e9ee8cf6f2d75c0e2847d548719e5ba07a47c67c1030a)

| tasks                       | filter    | expected      |&nbsp;|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do[36]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do[37]}|

[<i class="fa fa-info-circle"></i> More details](#2d2f4c1a070b3434226e9ee8cf6f2d75c0e2847d548719e5ba07a47c67c1030a)
""")

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
