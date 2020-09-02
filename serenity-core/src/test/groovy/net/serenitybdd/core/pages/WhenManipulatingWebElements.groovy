package net.serenitybdd.core.pages

import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.exceptions.ElementShouldBeEnabledException
import net.thucydides.core.webdriver.exceptions.ElementShouldBePresentException
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.pagefactory.ElementLocator
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.TimeUnit

/**
 * Created by john on 20/01/15.
 */
class WhenManipulatingWebElements extends Specification {


    WebDriverFacade driver = Mock()
    WebElement webElement = Mock()
    ElementLocator locator = Mock();
    PageObject parentPage = Mock();

    def setup() {
        StepEventBus.eventBus.clear()
    }

    @Unroll
    def "web element facade should be printed as the web element"() {
        given:
        webElement.getAttribute(attribute) >> attributeValue
        webElement.getTagName() >> tag
        locator.toString() >> "find by id or name 'value'"
        when:
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);
        then:
        new WebElementDescriber().webElementDescription(elementFacade, locator) == asString
        where:
        attribute | attributeValue | tag   | asString
        "id"      | "idvalue"      | "tag" | "<tag id='idvalue'> - find by id or name 'value'"
        "name"    | "somename"     | "tag" | "<tag name='somename'> - find by id or name 'value'"
        "class"   | "someclass"    | "tag" | "<tag class='someclass'> - find by id or name 'value'"
        "href"    | "link"         | "a"   | "<a href='link'> - find by id or name 'value'"

    }


    def "element facade should be printed using type and name if present"() {
        given:
        webElement.getAttribute("type") >> "button"
        webElement.getAttribute("value") >> "submit"
        webElement.getTagName() >> "input"
        locator.toString() >> "find by id or name 'value'"
        when:
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);
        then:
        new WebElementDescriber().webElementDescription(elementFacade, locator) == "<input type='button' value='submit'> - find by id or name 'value'"

    }

    def "web element facade should be printed as tag element if nothing available"() {
        given:
        webElement.getTagName() >> "tag"
        locator.toString() >> "find by id or name 'value'"
        when:
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);
        then:

        new WebElementDescriber().webElementDescription(elementFacade, locator) == "<tag> - find by id or name 'value'"
    }


    def "stale element should not be considered visible"() {
        given:
        webElement.isDisplayed() >> { throw new StaleElementReferenceException("Stale element") }
        when:
        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);
        then:
        !elementFacade.isVisible()
    }

    def "timeout can be redefined"() {
        when:
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);
        WebElementFacade webElementFacadeWithDifferentTimeout = webElementFacade.withTimeoutOf(2, TimeUnit.SECONDS);
        then:
        webElementFacadeWithDifferentTimeout.implicitTimeoutInMilliseconds == 2000L
        webElementFacadeWithDifferentTimeout.waitForTimeoutInMilliseconds == 2000L
    }

    JavascriptExecutorFacade mockJavascriptExecutorFacade = Mock()

    def "element can set window focus"() {
        given:
        def elementFacade = new WebElementFacadeImpl(driver, (ElementLocator) null, 100, 100) {
            @Override
            protected JavascriptExecutorFacade getJavascriptExecutorFacade() {
                return mockJavascriptExecutorFacade;
            }
        };
        when:
        elementFacade.setWindowFocus();
        then:
        1 * mockJavascriptExecutorFacade.executeScript("window.focus()")
    }

    @Unroll
    def "text value should be based on text and value attributes"() {

        when:
        webElement.isDisplayed() >> isDisplayed
        webElement.getText() >> getText
        webElement.getTagName() >> getTagName
        webElement.getAttribute("value") >> value

        WebElementFacade elementFacade = WebElementFacadeImpl.wrapWebElement(driver, webElement, 100, 100);

        then:
        elementFacade.getTextValue() == expectedTextValue

        where:
        isDisplayed | getText | getTagName | value   | expectedTextValue
        true        | null    | "input"    | "value" | "value"
        true        | null    | "input"    | null    | ""
        true        | "text"  | "textarea" | null    | "text"

    }

    def "Null WebElements should not be accessible"() {
        when:
        WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement) null, 100, 100);
        then:
        !webElementFacade.isVisible() &&
                !webElementFacade.isPresent() &&
                !webElementFacade.isEnabled()
    }

    WebElementFacade webElementFacade = WebElementFacadeImpl.wrapWebElement(driver, (WebElement) null, 100, 100);

    def "when webelement is null it should not be clickable"() {
        when:
            webElementFacade.click()
        then:
            thrown(ElementShouldBeEnabledException.class)
    }


    def "when webelement is null it should fail wait until present"() {
        when:
        webElementFacade.waitUntilPresent()
        then:
        thrown(ElementShouldBePresentException.class)
    }


    def "when webelement is null it should succeed waitUntilNotVisible"() {
        expect:
        webElementFacade.waitUntilNotVisible()
    }


    def "when webelement is null contains text should fail"() {
        expect:
            !webElementFacade.containsText("text")
    }

    def "when webelement is null contains selected value should fail"() {
        expect:
        !webElementFacade.containsSelectOption("value")
    }

    def "when webelement is null get text value should fail"() {
        when:
        !webElementFacade.getTextValue()
        then:
        thrown(ElementShouldBePresentException.class)
    }

    def "when webelement is null get value should fail"() {
        when:
        !webElementFacade.getValue()
        then:
        thrown(NoSuchElementException.class)
    }

}
