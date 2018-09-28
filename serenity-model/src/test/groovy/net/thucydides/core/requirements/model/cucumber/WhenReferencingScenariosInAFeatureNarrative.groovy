package net.thucydides.core.requirements.model.cucumber

import cucumber.runtime.io.MultiLoader
import cucumber.runtime.model.CucumberFeature
import spock.lang.Specification

class WhenReferencingScenariosInAFeatureNarrative extends Specification {

    def featureFile = "src/test/resources/serenity-cucumber/features/maintain_my_todo_list/filtering_todos.feature"
    def features = CucumberFeature.load(new MultiLoader(CucumberParser.class.getClassLoader()), [featureFile])
    def filteringTodoFeature = features[0].getGherkinFeature().feature

    def "Should be able to identify scenarios in a feature file by name"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("View only completed items").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("> Given that Jane has a todo list containing Buy some milk, Walk the dog  ")
        scenarioDescription.get().contains("> And she has completed the task called 'Walk the dog'  ")
        scenarioDescription.get().contains("> When she filters her list to show only Completed tasks  ")
        scenarioDescription.get().contains("> Then her todo list should contain Walk the dog  ")
    }

    def "Should report scenario outline given-when-then statements with variables"() {
        when:
        def scenarioDescription = ReferencedScenario.in(filteringTodoFeature).withName("Do many things").asGivenWhenThen()
        then:
        scenarioDescription.get().contains("> Given that Jane has a todo list containing &lt;tasks&gt;  ")
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
        def examples = ReferencedScenario.in(filteringTodoFeature).withName("Do many things").asExampleTable()
        then:
        examples.isPresent()
        and:
        examples.get() == """### Do some things
| tasks                       | filter    | expected      |**{result:Filtering things I need to do!Do many things}**|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do!Do many things[0]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do!Do many things[1]}|

[<i class="fa fa-info-circle"></i> More details](f5d0b08e3d3c0d0bf91e75595c812557a5edec10041ba70977bbead78fbdacf6.html)

### Do some other things
| tasks                       | filter    | expected      |**{result:Filtering things I need to do!Do many things}**|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do!Do many things[0]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do!Do many things[1]}|

[<i class="fa fa-info-circle"></i> More details](f5d0b08e3d3c0d0bf91e75595c812557a5edec10041ba70977bbead78fbdacf6.html)
"""
    }


    def "Should not change lines with no scenario references"() {
        expect:
            DescriptionWithScenarioReferences.from(filteringTodoFeature).forText("No scenario reference") == "No scenario reference"
    }

    def "Should replace scenario references with the Given-When-Then text"() {
        expect:
        DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("{Scenario} View only completed items") == """ > **View only completed items**  {result:Filtering things I need to do!View only completed items}
 > Given that Jane has a todo list containing Buy some milk, Walk the dog  
 > And she has completed the task called 'Walk the dog'  
 > When she filters her list to show only Completed tasks  
 > Then her todo list should contain Walk the dog  
[<i class="fa fa-info-circle"></i> More details](18e784fa28e919b434aa2fece7557e45321020135af83ca39d340c4f4830c003.html)
"""

    }

    def "Should ignore leading spaces"() {
        expect:
        DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("  {Scenario}   View only completed items") == """ > **View only completed items**  {result:Filtering things I need to do!View only completed items}
 > Given that Jane has a todo list containing Buy some milk, Walk the dog  
 > And she has completed the task called 'Walk the dog'  
 > When she filters her list to show only Completed tasks  
 > Then her todo list should contain Walk the dog  
[<i class="fa fa-info-circle"></i> More details](18e784fa28e919b434aa2fece7557e45321020135af83ca39d340c4f4830c003.html)
"""

    }

    def "Should replace example references with the example table"() {
        expect:
        DescriptionWithScenarioReferences.from(filteringTodoFeature).
                forText("{Examples} Do many things") ==  """### Do some things
| tasks                       | filter    | expected      |**{result:Filtering things I need to do!Do many things}**|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do!Do many things[0]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do!Do many things[1]}|

[<i class="fa fa-info-circle"></i> More details](f5d0b08e3d3c0d0bf91e75595c812557a5edec10041ba70977bbead78fbdacf6.html)

### Do some other things
| tasks                       | filter    | expected      |**{result:Filtering things I need to do!Do many things}**|
|-----------------------------|-----------|---------------|---|
| Buy some milk, Walk the dog | Completed | Walk the dog  |{example-result:Filtering things I need to do!Do many things[0]}|
| Buy some milk, Walk the dog | Active    | Buy some milk |{example-result:Filtering things I need to do!Do many things[1]}|

[<i class="fa fa-info-circle"></i> More details](f5d0b08e3d3c0d0bf91e75595c812557a5edec10041ba70977bbead78fbdacf6.html)
"""

    }



    def "should render narratives with example tables"() {
        given:
            CucumberParser parser = new CucumberParser()
        when:
            def narrative = parser.loadFeatureNarrative(new File(featureFile))
        then:
            narrative.isPresent()
    }

}