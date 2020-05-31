package net.serenitybdd.core.webdriver


import net.serenitybdd.core.pages.PageObject
import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenOpeningAPageOnASpecificUrl extends Specification {

    def driver = Mock(WebDriver)

    @DefaultUrl("http://my.app")
    class PageObjectWithADefaultUrl extends PageObject {
        PageObjectWithADefaultUrl(driver) { super(driver) }
        PageObjectWithADefaultUrl(WebDriver driver, MockEnvironmentVariables env) { super(driver, env) }
    }

    class OrdinaryPageObject extends PageObject {
        OrdinaryPageObject(driver) { super(driver) }
        OrdinaryPageObject(WebDriver driver, EnvironmentVariables env) { super(driver, env) }
    }

    def "should use the @DefaultUrl by default"() {
        given:
            def page = new PageObjectWithADefaultUrl(driver)
        when:
            page.open()
        then:
            1*driver.get("http://my.app")
    }

    def "should open on a specified URL if requested"() {
        given:
          def page = new PageObjectWithADefaultUrl(driver)
        when:
          page.openAt("http://some.other.url")
        then:
         1*driver.get("http://some.other.url")
    }

    def "should use the base url if specified"() {
        given:
            def env = new MockEnvironmentVariables()
            env.setProperty("webdriver.base.url","http://localhost:8080")
        and:
            def page = new PageObjectWithADefaultUrl(driver, env)
        when:
            page.openAt("http://some.other.url/path")
        then:
            1*driver.get("http://localhost:8080/path")

    }

    def "should switch to a relative path if provided"() {
        given:
        def page = new PageObjectWithADefaultUrl(driver)
        when:
        page.openAt("/relative-path")
        then:
        1*driver.get("http://my.app/relative-path")

    }


    def "should use the base url with a relative path if specified"() {
        given:
            def env = new MockEnvironmentVariables()
            env.setProperty("webdriver.base.url","http://localhost:8080")
        and:
            def page = new PageObjectWithADefaultUrl(driver, env)
        when:
            page.openAt("/relative-path")
        then:
            1*driver.get("http://localhost:8080/relative-path")

    }

    def "should use the base url from the system conf with a relative path if specified"() {
        given:
        def env = new MockEnvironmentVariables()
        env.setProperty("webdriver.base.url","http://localhost:8080")
        and:
        def page = new OrdinaryPageObject(driver, env)
        when:
        page.openAt("/relative-path")
        then:
        1*driver.get("http://localhost:8080/relative-path")

    }

    def "should open a named URL defined in the serenity.conf file"() {
        given:
        EnvironmentVariables environment = new MockEnvironmentVariables()
        environment.setProperty("pages.mypage","http://my.page")
        def page = new OrdinaryPageObject(driver,environment)
        when:
        page.openPageNamed("mypage")
        then:
        1*driver.get("http://my.page")
    }

    def "should open an environment-specific named URL defined in the serenity.conf file"() {
        given:
        EnvironmentVariables environment = new MockEnvironmentVariables()
        environment.setProperty("pages.mypage","http://my.page")
        environment.setProperty("environments.dev.pages.mypage","http://my.dev.page")
        environment.setProperty("environment","dev")
        def page = new OrdinaryPageObject(driver,environment)
        when:
        page.openPageNamed("mypage")
        then:
        1*driver.get("http://my.dev.page")
    }

}
