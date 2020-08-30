package net.thucydides.core.webdriver

import net.thucydides.core.webdriver.DefaultPageObjectInitialiser
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import sample.page.TestPage
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

@Ignore
class WhenUsingWebElementFacadeExtender extends Specification {
	
	@Shared
	def driver = new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory()) // new HtmlUnitDriver();//

	@Shared
	def page = new TestPage(driver)

	def setupSpec() {
		new DefaultPageObjectInitialiser(driver, 1000).apply(page);
		page.open()
	}

    def "WebElementFacade methods can be defined in a page object"(){
        when: "instantiating a page object with WebElementFacade fields"
            def page = new TestPage(driver)
            new DefaultPageObjectInitialiser(driver, 1000).apply(page);
        then: "the annotated fields should be instantiated"
            page.elementFirst != null
    }

//    @Ignore("Fragile test: to review")
	def "WebElementFacade methods should be able to be called on Extender"(){
		when: "calling WebElementFacade method"

		then: "field should be accessible"
			page.elementFirst.getTagName() == "input"
	}

//    @Ignore("Fragile test: to review")
	def "Extender methods should be able to be called"(){
		when: "calling WebElementFacadeInput method"
			page.elementLast.enterText("text")
		then: "text should be entered"
			page.elementLast.getAttribute("value").equals("text")
	}

	def cleanupSpec() {
		if (driver) {
			driver.close()
			driver.quit()
		}
	}
}
	
