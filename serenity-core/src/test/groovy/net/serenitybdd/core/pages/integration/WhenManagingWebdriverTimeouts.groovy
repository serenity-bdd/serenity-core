package net.serenitybdd.core.pages.integration

import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.core.time.Stopwatch
import net.serenitybdd.core.webdriver.servicepools.ChromeServicePool
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool
import net.thucydides.core.pages.integration.StaticSitePage
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.steps.StepFailure
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.exceptions.ElementShouldBeDisabledException
import net.thucydides.core.webdriver.exceptions.ElementShouldBeEnabledException
import net.thucydides.core.webdriver.exceptions.ElementShouldBeInvisibleException
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.*

import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

import static java.time.temporal.ChronoUnit.SECONDS

/**
 * Timeouts are highly configurable in Serenity.
 *
 * The first level of timeouts is related to the standard WebDriver implicit waits
 * (see http://docs.seleniumhq.org/docs/04_webdriver_advanced.jsp#implicit-waits).
 * <quote>"An implicit wait is to tell WebDriver to poll the DOM for a certain amount of
 * time when trying to find an element or elements if they are not immediately available."</quote>
 * The WebDriver default is 0. It can be overriden using the webdriver.timeouts.implicitlywait system property.
 *
 *
 */
class WhenManagingWebdriverTimeouts extends Specification {

    @Shared DriverServicePool driverService;

    @Shared
    WebDriver driver

    def setupSpec() {
        driverService = new ChromeServicePool()
        driverService.start()
    }

    def cleanupSpec() {
        driverService.shutdown()
    }

    WebDriver newDriver() {
        def desiredCapabilities = DesiredCapabilities.chrome();
        def chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        driver = driverService.newDriver(desiredCapabilities);
        return driver
    }

    def setup() {
        StepEventBus.eventBus.clear()
        if (driver == null) {
            driver = newDriver()
        }
        driver.navigate().refresh();
    }

    //
    // IMPLICIT WAITS
    //
    def "WebDriver implicit waits are defined using the webdriver.timeouts.implicitlywait system property"() {
        given: "The #slow-loader field takes 4 seconds to load"
        and: "We configure the WebDriver implicit wait to be 0 seconds"
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0"])
        when: "We access the field"
            page.slowLoadingField.isDisplayed()
        then: "An error should be thrown"
            thrown(org.openqa.selenium.ElementNotInteractableException)
    }

    def "Slow loading fields should not wait once a step has failed"() {
        given: "The #slow-loader field takes 4 seconds to load"
            def page = openStaticPage()
        and: "A step has failed"
            def stepFailure = Mock(StepFailure)
            StepEventBus.getEventBus().testStarted("a test")
            StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("a step"))
            StepEventBus.getEventBus().stepFailed(stepFailure);
        when: "We access the field"
            Stopwatch stopwatch = new Stopwatch()
            stopwatch.start()
            page.verySlowLoadingField.isDisplayed()
        then: "No error should be thrown"
            notThrown(org.openqa.selenium.ElementNotInteractableException)
        and: "the response should be returned instantly"
            stopwatch.stop() < 100
    }

    private StaticSitePage openStaticPageWith(Map<String, String> variables) {
        def environmentVariables = new MockEnvironmentVariables();
        for(String key : variables.keySet()) {
            environmentVariables.setProperty(key, variables[key]);
        }
        WebDriverFacade driverFacade = new WebDriverFacade(driver, new WebDriverFactory(), environmentVariables);
        def page = new StaticSitePage(driverFacade, environmentVariables)
        page.open()
        return page
    }

    private StaticSitePage openStaticPage() {
        def driver = new WebDriverFacade(driver, new WebDriverFactory(), new SystemEnvironmentVariables()); // HtmlUnitDriver();
        def page = new StaticSitePage(driver)
        page.open()
        return page
    }

    def "The default implicit wait is set to 2 seconds"() {
        given: "The #city field takes 500 ms to load"
            def page = openStaticPage()
        when: "We access the field using the default implicit wait timeouts"
        then: "The field should be retrieved correctly"
            page.city.isDisplayed()
    }

    def "The implicit waits apply when you find a list of elements"() {
        when: "We access the a list of elements"
            def page = openStaticPage()
            int itemCount = page.elementItems.size()
        then: "They should all be found"
            itemCount == 4
    }

    def "If the implicit wait times out when fetching a list of values only the currently loaded values will be returned"() {
        given: "We configure the WebDriver implicit wait to be 0 milliseconds"
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0"])
        when: "We access the a list of elements"
            int itemCount = page.elementItems.size()
        then: "Only the elements loaded after the timeout should be loaded"
            itemCount == 0
    }

    def "You can force an extra delay to give elements time to load"() {
        given: "We configure the WebDriver implicit wait to be 0 milliseconds"
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0", "webdriver.wait.for.timeout":"0"])
        when: "We access the a list of elements"
            def count = page.withTimeoutOf(5, SECONDS).waitFor(page.elementItems).size()
        then: "Only the elements loaded after the timeout should be loaded"
            count == 4
    }

    @Timeout(20)
    def "You can override the implicit wait during test execution"() {
        given: "The #slow-loader field takes 3 seconds to load"
            def page = openStaticPage()
        when: "We override the implicit timeout to allow the slow-loader field to load"
            page.setImplicitTimeout(10, SECONDS)
        then: "we should be able to access the slow-loader field"
            page.firstElementItem.isVisible()
        and: "we can reset the driver timeouts to the default value once we are done"
            page.resetImplicitTimeout()
        and:
            !page.fieldDoesNotExist.isVisible()
    }

    def "Implicit timeout should not be affected by isCurrently* methods"() {
        given: "The #slow-loader WebElementFacade field takes 3 seconds to load"
        def page = openStaticPage()
        when: "We override the implicit timeout to allow the slow-loader field to load"
        page.setImplicitTimeout(10, SECONDS)
        then: "isCurrently* methods should not use the implicit timeout"
        !page.slowLoadingField.isCurrentlyVisible()
    }


    // Fixme
    def "Element loading times should not be affected by isCurrently* methods"() {
        given: "The #slow-loader field takes 3 seconds to load"
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"5000"])
        when: "we should be able to access the slow-loader field"
            page.country.isCurrentlyVisible()
        then: "we can reload a slow element normally"
            page.slowLoadingField.isDisplayed()
    }

    @Unroll
    def "When you check the visibility of a field using isVisible() Serenity should use the current implicit wait timeout (#field)"() {
        given:
            def page = openStaticPage()
        when: "We check the visibility of a slow-loading field"
            def visibility = page."$field".isVisible()
        then:
            visibility == expectedVisibility
        where:
            field                | expectedVisibility
            "hiddenField"        | false                 // Invisible
            "firstName"          | true                  // Immediately visible
            "city"               | true                  // loads in 500 ms
    }

    @Unroll
    def "If you want to check the current visibility of a field using isCurrentlyVisible() Serenity will return a result immediately"() {
        given:
            def page = openStaticPage()
        when: "We check the visibility of a slow-loading field"
            def visibility = page."$field".isCurrentlyVisible()
        then:
            visibility == expectedVisibility
        where:
            field                | expectedVisibility
            "hiddenField"        | false                 // Invisible
            "firstName"          | true                  // Immediately visible
            "city"               | false                 // loads in 500 ms
    }

    def "The webdriver.timeouts.implicitlywait value is used when loading elements using the findAll() method."() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"5"])
        when: "We fetch a list of elements using findElements"
            def elements = page.findAll(By.cssSelector("#elements option"))
        then:
            elements.isEmpty()
    }


    def "The webdriver.timeouts.implicitlywait value is used when loading non-existant elements"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50"])
        when: "We check for an element that does not exist"
            def elementVisible = page.isElementVisible(By.cssSelector("#does-not-exist"))
        then:
            !elementVisible
    }


    //
    // WAIT-FOR TIMEOUTS
    //
    def "You can also explicitly wait for fields to appear. This will use the webdriver.wait.for.timeout property rather than the implicit timeouts"() {
        given: "We set the webdriver.wait.for.timeout to 5 seconds"
            def page = openStaticPageWith(["webdriver.implicit.wait":"0", "webdriver.wait.for.timeout":"5000"])
        when: "We wait for a field to appear that takes 2 seconds to load"
            page.slowLoadingField.waitUntilVisible()
        then:
            page.slowLoadingField.isCurrentlyVisible()
    }

    def "Waiting for a field will fail if it exceeds the wait.for.timeout value"() {
        given: "We set the webdriver.wait.for.timeout to 1 seconds and The implicit wait will timeout"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "1000", "webdriver.timeouts.implicitlywait":"0"])
        when: "We wait for a field to appear that takes 2 seconds to load"
            page.slowLoadingField.waitUntilVisible()
        then:
            thrown(NoSuchElementException)
    }

    def "You can wait for elements to be not visible"() {
        given:
            def page = openStaticPage()
        when:
            page.placetitle.waitUntilNotVisible()
        then:
            !page.placetitle.isCurrentlyVisible()

    }

    def "You can wait for elements to be enabled"() {
        given:
            def page = openStaticPage()
        when:
            page.initiallyDisabled.waitUntilEnabled()
        then:
            page.initiallyDisabled.isCurrentlyEnabled()
    }


    def "You can wait for elements to be disabled"() {
        given:
            def page = openStaticPage()
        when:
            page.initiallyEnabled.waitUntilDisabled()
        then:
            !page.initiallyEnabled.isCurrentlyEnabled()
    }

    def "The wait.for.timeout applies for checking methods like isElementVisible()"() {
        given:
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "50"])
        when:
            def cityIsVisible = page.isElementVisible(By.cssSelector("#city"))
        then:
            !cityIsVisible
    }

    def "The default wait.for.timeout will work checking methods like isElementVisible() with slow-loadding fields"() {
        given:
            def page = openStaticPage()
        when:
            def cityIsVisible = page.isElementVisible(By.cssSelector("#city"))
        then:
            cityIsVisible
    }

    def "The waitUntilDisabled method can be configured (globally) using the webdriver.wait.for.timeout property"() {
        given: "We set the webdriver.wait.for.timeout to a low value"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "100"])
        when: "we wait for a field to be disabled"
            page.initiallyEnabled.waitUntilDisabled()
        then: "the action should timeout"
            thrown(ElementShouldBeDisabledException)
    }

    def "The waitUntilEnabled method can be configured (globally) using the webdriver.wait.for.timeout property"() {
        given: "We set the webdriver.wait.for.timeout to 1 seconds"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "100"])
        when:
            page.initiallyDisabled.waitUntilEnabled()
        then:
            thrown(ElementShouldBeEnabledException)
    }


    def "The waitUntilVisible method can be configured (globally) using the webdriver.wait.for.timeout property"() {
        given: "We set the webdriver.wait.for.timeout to 1 seconds"
            def page = openStaticPageWith(["webdriver.wait.for.timeout":"100"])
        when:
            page.city.waitUntilVisible()
        then:
            thrown(NoSuchElementException)
    }

    def "The waitUntilInvisible method can be configured (globally) using the webdriver.wait.for.timeout property"() {
        given: "We set the webdriver.wait.for.timeout to 1 seconds"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "100"])
        when:
            page.placetitle.waitUntilNotVisible()
        then:
            thrown(ElementShouldBeInvisibleException)
    }


    //
    // Using the withTimeoutOf() methods
    //

    def "The withTimeoutOf() method can be used to override the global webdriver.wait.for.timeout value"() {
        given:
            def page = openStaticPage()
        when:
            def cityIsDisplayed = page.withTimeoutOf(50, ChronoUnit.MILLIS).elementIsDisplayed(By.cssSelector("#city"))

        then:
            !cityIsDisplayed
    }

    def "The withTimeoutOf() method can be used to wait until a button is clickable"() {
        given:
            def page = openStaticPage()
        when:
            page.initiallyDisabled.withTimeoutOf(10, TimeUnit.SECONDS).waitUntilClickable().click()
        then:
            noExceptionThrown()
    }

    def "The withTimeoutOf() method can be used to wait until a button is clickable and will fail if it waits too long"() {
        given:
            def page = openStaticPage()
        when:
            page.initiallyDisabled.withTimeoutOf(50, TimeUnit.MILLISECONDS).waitUntilClickable().click()
        then:
            thrown(TimeoutException)
    }

    def "The withTimeoutOf() method can be used to override the global webdriver.wait.for.timeout value (positive case)"() {
        given:
            def page = openStaticPage()
        when:
            def cityIsDisplayed = page.withTimeoutOf(2, SECONDS).elementIsDisplayed(By.cssSelector("#city"))
        then:
            cityIsDisplayed
    }

    def "The withTimeoutOf() method can be used to modify the timeout for elementIsPresent methods"() {
        given:
            def page = openStaticPage()
        when:
            def cityIsPresent = page.withTimeoutOf(2, SECONDS).elementIsPresent(By.cssSelector("#city"))
        then:
            cityIsPresent
    }


    def "The withTimeoutOf() method can be used to override the global webdriver.wait.for.timeout value for elements"() {
        given:
            def page = openStaticPage()
        when:
           page.placetitle.withTimeoutOf(50, TimeUnit.MILLISECONDS).waitUntilNotVisible()
        then:
            thrown(ElementShouldBeInvisibleException)
    }


    def "The withTimeoutOf() method can be used to override the global timeouts when waiting lists"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
            page.withTimeoutOf(10, SECONDS).waitForPresenceOf(By.cssSelector("#elements option"))
        then:
            page.elementItems.size() == 4
    }

    // fixme
    def "The withTimeoutOf() method can be used to override the global timeouts for elements"() {
        given:
            def page = openStaticPageWith(["webdriver .timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
            page.withTimeoutOf(10, SECONDS).waitFor(By.cssSelector("#city"))
        then:
            page.city.isCurrentlyVisible()
            page.isElementVisible(By.cssSelector("#city"))
    }


    @Timeout(5)
    def "Should not hang if CSS selector is incorrect"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
            page.waitFor("NOT!%**##CSS")
        then:
            thrown(Exception)
    }

    def "The withTimeoutOf() method can be used to override the global timeouts when retrieving lists"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
            def elements = page.withTimeoutOf(10, SECONDS).findAll("#elements option")
        then:
            elements.size() == 4
    }

    @Ignore("Tends to fail on Jenkins if the build is too slow")
    def "The withTimeoutOf() method can be used to reduce the global timeouts when retrieving lists"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"5000","webdriver.wait.for.timeout": "5000"])
        when:
            def elements = page.withTimeoutOf(0, SECONDS).findAll("#elements option")
        then:
            elements.size() == 0
    }


    def "The withTimeoutOf() method can be used to override the global timeouts when retrieving elements"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
            WebElementFacade city = page.withTimeoutOf(10, SECONDS).find("#city")
        then:
            city.isCurrentlyVisible()
    }

    def "The find() method after withTimeout can take parameters"() {
        given:
        def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"50","webdriver.wait.for.timeout": "50"])
        when:
        WebElementFacade city = page.withTimeoutOf(10, SECONDS).find("#{0}","city")
        then:
        city.isCurrentlyVisible()
    }

    def "waitForAbsenceOf should return immediately if no elements are present"() {
        when:
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "50000"])
        then:
            page.waitForAbsenceOf("#does-not-exist")
    }

    def "waitForAbsenceOf should wait no more than the time needed for the element to dissapear"() {
        when: "placetitle will dissapear after 2 seconds"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "10000"])
        then:
            page.waitForAbsenceOf("#placetitle")
    }

    def "waitForAbsenceOf with explicit timeout should wait no more than the time needed for the element to dissapear"() {
        given: "placetitle will dissapear after 3 seconds"
            def page = openStaticPageWith(["webdriver.wait.for.timeout": "10000"])
        when:
            page.withTimeoutOf(1, SECONDS).waitForAbsenceOf("#placetitle")
        then:
            thrown(org.openqa.selenium.TimeoutException)
    }

    def "Timeouts for individual fields can be specified using the timeoutInSeconds parameter of the FindBy annotation"() {
        given:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0"])
        when:
            page.country.isDisplayed()
        then: "Annotated timeouts on fields override configured implicit timeouts"
            page.country.isVisible()
    }

    def "You can check whether a child element is present using a By selector"() {
        when:
        def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0"])
        then:
        page.clients.shouldContainElements(By.cssSelector(".color"))
        and:
        page.clients.shouldContainElements(".color")
    }

    def "You can check whether a child element is present"() {
        when:
            def page = openStaticPageWith(["webdriver.timeouts.implicitlywait":"0"])
        then:
            page.clients.containsElements(By.cssSelector(".color"))
        and:
            !page.clients.containsElements(By.cssSelector(".flavor"))
    }

    def "You can check whether a child element is present with waits"() {
        when:
            def page = openStaticPage()
        then:
            page.clients.withTimeoutOf(0, TimeUnit.SECONDS).containsElements(By.cssSelector(".color"))
    }

}
