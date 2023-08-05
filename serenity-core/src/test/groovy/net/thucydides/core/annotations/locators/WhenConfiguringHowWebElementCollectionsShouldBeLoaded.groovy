package net.thucydides.core.annotations.locators

import net.thucydides.core.WebdriverCollectionStrategy
import net.thucydides.model.environment.MockEnvironmentVariables
import org.openqa.selenium.WebElement
import spock.lang.Specification

import static net.thucydides.core.WebdriverCollectionStrategy.*

class WhenConfiguringHowWebElementCollectionsShouldBeLoaded extends Specification {

    def "the default strategy should be Optimistic for legacy reasons"() {
        when:
        def environmentVariables = new MockEnvironmentVariables()
        then:
        WebdriverCollectionStrategy.definedIn(environmentVariables) == Optimistic
    }

    def "the strategy can be defined in using the serenity.webdriver.collection_loading_strategy property"() {
        given:
        def environmentVariables = new MockEnvironmentVariables()
        when:
        environmentVariables.setProperty("serenity.webdriver.collection_loading_strategy", strategy)
        then:
        WebdriverCollectionStrategy.definedIn(environmentVariables) == configuredStrategy
        where:
        strategy      | configuredStrategy
        "Pessimistic" | Pessimistic
        "Optimistic"  | Optimistic
        "Paranoid"    | Paranoid
        "pessimistic" | Pessimistic
        "OPTIMISTIC"  | Optimistic
        "Misspelt"    | Optimistic
    }


    def visibleElement = Mock(WebElement)
    def invisibleElement = Mock(WebElement)

    def setup() {
        visibleElement.isDisplayed() >> true
        invisibleElement.isDisplayed() >> false
    }

    def "Optimistic, Pessimistic and Paranoid loading will wait for the collection field to be defined"() {

        expect:
        WaitForWebElementCollection.accordingTo(strategy).areElementsReadyIn(webElementsField) == consideredUsable
        where:
        strategy    | webElementsField | consideredUsable
        Optimistic  | null             | false
        Pessimistic | null             | false
        Paranoid    | null             | false

        Pessimistic | []               | true
        Optimistic  | []               | true
        Paranoid    | []               | true

    }

    def "Optimistic loading should succeed as long as the WebElements field is defined"() {
        expect:
        WaitForWebElementCollection.accordingTo(Optimistic).areElementsReadyIn([invisibleElement, invisibleElement]) == true
        WaitForWebElementCollection.accordingTo(Optimistic).areElementsReadyIn([visibleElement, invisibleElement]) == true
        WaitForWebElementCollection.accordingTo(Optimistic).areElementsReadyIn([visibleElement, visibleElement]) == true
    }

    def "Pessimistic loading requires at least the first element in the list to be displayed"() {
        expect:
        WaitForWebElementCollection.accordingTo(Pessimistic).areElementsReadyIn([invisibleElement, invisibleElement]) == false
        WaitForWebElementCollection.accordingTo(Pessimistic).areElementsReadyIn([visibleElement, invisibleElement]) == true
        WaitForWebElementCollection.accordingTo(Pessimistic).areElementsReadyIn([visibleElement, visibleElement]) == true
    }

    def "Paranoid loading needs all the elements in the list is displayed"() {
        expect:
        WaitForWebElementCollection.accordingTo(Paranoid).areElementsReadyIn([invisibleElement, invisibleElement]) == false
        WaitForWebElementCollection.accordingTo(Paranoid).areElementsReadyIn([visibleElement, invisibleElement]) == false
        WaitForWebElementCollection.accordingTo(Paranoid).areElementsReadyIn([visibleElement, visibleElement]) == true

    }
}
