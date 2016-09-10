package net.serenitybdd.screenplay.questions

import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.questions.targets.TheTarget
import net.serenitybdd.screenplay.targets.Target
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.StepEventBus
import org.openqa.selenium.WebDriver
import spock.lang.Specification

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat
import static org.hamcrest.Matchers.equalTo

class WhenAskingQuestionsAboutUIElements extends Specification {

    def target = Mock(Target)
    def browser = Mock(WebDriver)
    def element = Mock(WebElementFacade)
    def element2 = Mock(WebElementFacade)
    def actor = Actor.named("James")
    def steplistener = Mock(BaseStepListener)

    def setup() {
        target.resolveFor(actor) >> element
        target.resolveAllFor(actor) >> [element, element2]
    }


    def "Actor should be able to ask direct questions about targets"() {
        when:
            element.getValue() >> "some value"
        and:
            actor.can(BrowseTheWeb.with(browser))
            aTestHasStarted()
        then:
            actor.should(seeThat("a value", TheTarget.valueOf(target), equalTo("some value")))

    }

    def "should read string values from targets"() {
        when:
            element.getValue() >> "some value"
        and:
            Question<String> question = TheTarget.valueOf(target);
        then:
            question.answeredBy(actor) == "some value"
    }

    def "should read string text from targets"() {
        when:
            element.getText() >> "some text value"
        and:
            Question<String> question = TheTarget.textOf(target);
        then:
            question.answeredBy(actor) == "some text value"
    }

    def "should read selected values from targets"() {
        when:
            element.getSelectedValue() >> "some text value"
        and:
            Question<String> question = TheTarget.selectedValueOf(target);
        then:
            question.answeredBy(actor) == "some text value"
    }

    def "should read selected visible text values from targets"() {
        when:
            element.getSelectedVisibleTextValue() >> "some text value"
        and:
            Question<String> question = TheTarget.selectedVisibleTextValueOf(target);
        then:
            question.answeredBy(actor) == "some text value"
    }

    def "should read selected options from targets"() {
        when:
            element.selectOptions >> ["value1","value2"]
        and:
            Question<String> question = TheTarget.selectedOptionsOf(target);
        then:
            question.answeredBy(actor) == ["value1","value2"]
    }

    def "should read selected attribute values from targets"() {
        when:
            element.getAttribute("attr") >> "some text value"
        and:
            Question<String> question = TheTarget.attributeNamed("attr").forTarget(target);
        then:
            question.answeredBy(actor) == "some text value"
    }

    def "should read selected CSS values from targets"() {
        when:
            element.getCssValue("color") >> "red"
        and:
            Question<String> question = TheTarget.cssValueNamed("color").forTarget(target);
        then:
            question.answeredBy(actor) == "red"
    }

    def aTestHasStarted() {
        StepEventBus.eventBus.registerListener(steplistener)
        StepEventBus.eventBus.testStarted("Some test")
    }
}
