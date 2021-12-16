package net.thucydides.core.annotations.findby

import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.chrome.ChromeDriver
import sample.page.TestPage
import spock.lang.Shared
import spock.lang.Specification


class WhenFindingExtendedWebElementFacade extends Specification {
	
	@Shared
	def driver =  new WebDriverFacade(ChromeDriver.class, new WebDriverFactory())
		
	def "WebElementFacade Extender should be found with selenium FindBy"(){
		
		when: "page is initialized"
			def page = new TestPage(driver)
			
		then: "WebElementFacade extender with Smart FindBy should be proxied"
			page.elementFirst != null
			
	}
	
	def "WebElementFacade Extender should be found with Smart FindBy"(){
		
		when: "page is initialized"
			def page = new TestPage(driver)
			
		then: "WebElementFacade extender with Smart FindBy should be proxied"
			page.elementLast != null
			
	}


}
