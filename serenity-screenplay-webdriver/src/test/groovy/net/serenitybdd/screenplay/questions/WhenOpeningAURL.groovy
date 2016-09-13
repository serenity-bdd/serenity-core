package net.serenitybdd.screenplay.questions

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.actions.Open
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenOpeningAURL extends Specification {

    def james = Actor.named("James")

    def driver = Mock(WebDriver)

    def setup() {
        james.can(BrowseTheWeb.with(driver))
    }

    def "should open a provided URL"() {
        given:
            Performable openUrl = Open.url("http://www.mysite.com/myapp")
        when:
            openUrl.performAs(james);
        then:
            1 * driver.get("http://www.mysite.com/myapp");
    }
}
