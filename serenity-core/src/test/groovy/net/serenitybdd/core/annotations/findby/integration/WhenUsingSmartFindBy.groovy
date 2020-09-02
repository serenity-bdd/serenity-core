package net.serenitybdd.core.annotations.findby.integration

import net.serenitybdd.core.annotations.findby.By
import net.thucydides.core.pages.integration.StaticSitePageWithFacades
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.phantomjs.PhantomJSDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingSmartFindBy extends Specification {

    @Shared
    def driver = new PhantomJSDriver();

    @Shared
    def page = new StaticSitePageWithFacades(driver, 1000)

    def setupSpec() {
        page.open()
        page.waitFor(1).second()

    }

	def "should be able to find an element using jquery"(){

		when: "we try to find an element using a jquery selector"
			def element = driver.findElement(By.jquery("#firstname"))

        then: "we should find the element"
            element.getAttribute("value")  == "<enter first name>"
	}


    def "should be able to find an element using button text"(){

        when: "we try to find an element using button text"
        def element = driver.findElement(By.buttonText("Red Button"))

        then: "we should find the element"
        element.getAttribute("id")  == "red-button"
    }

    def "should throw exception if button not found"(){

        when: "we try to find an element using button text"
        def element = driver.findElement(By.buttonText("Missing Button"))

        then:
            thrown(NoSuchElementException)
    }

    def "should be able to find elements using button text"(){

        when: "we try to find an element using button text"
        def elements = driver.findElements(By.buttonText("Blue's Button"))

        then: "we should find the element"
        elements.size() == 1
    }


    def "should return an empty list if no matching buttons found"(){

        when: "we try to find elements using button text"
        def elements = driver.findElements(By.buttonText("Mission Button"))

        then: "we should not find the element"
        elements.size() == 0
    }

    def "should be able to find a nested element using jquery"(){

        when: "we try to find an element using a jquery selector"
        	def element = driver.findElement(By.jquery("#firstname"))

        then: "we should find the element"
        element.getAttribute("value")  == "<enter first name>"
    }

    def "should be able to find multiple elements using jquery"(){

        when: "we try to find several elements using a jquery selector"
            def optionList = driver.findElements(By.jquery("#multiselect option"))

        then: "we should find a list of elements"
            optionList.size == 5
    }


    def "an element should fail gracefully if the jquery search fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElement(By.jquery("#does_not_exist"))

        then: "element should not be found"
            thrown(NoSuchElementException)
    }

    def "an element should fail gracefully if the jquery search for multiple elements fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElements(By.jquery("#does_not_exist"))

        then: "element should not be found"
            thrown(NoSuchElementException)
    }

    def cleanupSpec() {
        if (driver) {
            driver.close()
            driver.quit()
        }
    }



}
