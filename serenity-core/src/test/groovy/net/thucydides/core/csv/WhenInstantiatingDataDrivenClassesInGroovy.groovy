package net.thucydides.core.csv

import net.serenitybdd.annotations.Step
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.steps.StepFactory
import spock.lang.Specification

class WhenInstantiatingDataDrivenClassesInGroovy extends Specification {

    class Person {
        String name
        String age
    }

    def "should set fields on Groovy classes"() {
        given: "a Groovy object"
            def person = new Person()
        when:
            InstanceBuilder.inObject(person).setPropertyValue("name","joe")
        then:
            person.name == "joe"
    }

    def "should set fields on Groovy classes using capitalized field names"() {
        given: "a Groovy object"
            def person = new Person()
        when:
            InstanceBuilder.inObject(person).setPropertyValue("Name","joe")
        then:
            person.name == "joe"
    }

    def "should set fields on proxied Groovy"() {
        given: "a Groovy object"
            def pages = Mock(Pages)
            def stepFactory = new StepFactory(pages);
            PersonSteps steps = stepFactory.getNewStepLibraryFor(PersonSteps)
        when:
            InstanceBuilder.inObject(steps).setPropertyValue("name","joe")
        then:
            steps.name == "joe"
    }

    def "should set fields on proxied setters in Groovy"() {
        given: "a Groovy object"
            def pages = Mock(Pages)
            def stepFactory = new StepFactory(pages);
            PersonSteps steps = stepFactory.getNewStepLibraryFor(PersonSteps)
        when:
            InstanceBuilder.inObject(steps).setPropertyValue("favoriteColor","red")
        then:
            steps.color == "red"
    }

}

class PersonSteps extends ScenarioSteps {
    String name
    String age
    String color

    def setFavoriteColor(color) {
        this.color = color
    }

    PersonSteps(Pages pages) {
        super(pages)
    }

    @Step
    def doStuff() {}
}
