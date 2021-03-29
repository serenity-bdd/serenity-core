package serenityscreenplay.net.serenitybdd.screenplay.questions


import serenitycore.net.serenitybdd.core.Serenity
import serenityscreenplay.net.serenitybdd.screenplay.Actor
import serenitymodel.net.thucydides.core.model.Story
import serenitycore.net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

import static serenityscreenplay.net.serenitybdd.screenplay.GivenWhenThen.seeThat
import static org.hamcrest.Matchers.equalTo

class WhenRememberingAnswers extends Specification {

    def setup() {
        Serenity.initialize(this);
        StepEventBus.getEventBus().testSuiteStarted(Story.called("sample story"));
        StepEventBus.getEventBus().testStarted("sample test");
    }

    def "should be able to make assertions about values previously remembered"() {
        given:
            Actor tracy = Actor.named("Tracy")
        when:
            tracy.remember("age",30)
        then:
            tracy.should(seeThat(Remembered.valueOf("age"), equalTo(30)))
    }

    def "should be able to make assertions about different types"() {
        given:
            Actor tracy = Actor.named("Tracy")
        when:
            tracy.remember("color","Red")
        then:
            tracy.should(seeThat(Remembered.valueOf("color"), equalTo("Red")))
    }

}
