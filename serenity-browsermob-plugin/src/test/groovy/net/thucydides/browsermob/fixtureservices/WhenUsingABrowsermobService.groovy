package net.thucydides.browsermob.fixtureservices

import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService
import net.thucydides.core.fixtureservices.FixtureException
import net.thucydides.model.environment.MockEnvironmentVariables
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Specification

class WhenUsingABrowsermobService extends Specification {

    BrowserMobFixtureService service
    BrowserMobFixtureService service2

    def capabilities = Mock(DesiredCapabilities)
    def environmentVariables = new MockEnvironmentVariables()

    def "should find the browsermob service if it is on the classpath"() {
        given:
            def classpathFixtureProviderService = new ClasspathFixtureProviderService()
        when:
            def services = classpathFixtureProviderService.getFixtureServices()
        then:
            services.find {
                it.class == BrowserMobFixtureService
            }
    }

    def "should start a browsermob server"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)

        when:
            service.setup()

        then:
            service.proxyServer
    }

    def "should shut down a browsermob server"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)

        when:
            service.setup()
            service.shutdown()

        then:
            !service.proxyServer
    }

    def "should only activate browsermob for requested browsers if specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
            environmentVariables.setProperty("webdriver.driver","firefox")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            !service.proxyServer
    }

    def "should activate browsermob for requested browsers if specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
            environmentVariables.setProperty("webdriver.driver","iexplorer")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            service.proxyServer
    }

    def "should activate browsermob if no driver is specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            service.proxyServer
    }

    /*def "should configure capabilities with the browser mob proxy config"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)
            service.setup()
        when:
            service.addCapabilitiesTo(capabilities)
        then:
            1 * capabilities.setCapability('proxy', _)
    }*/

    def "initial port used should be the default port"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()
        then:
            service.port == BrowserMobFixtureService.DEFAULT_PORT
    }


    def "default initial port can be overridden using the environment variables"() {
        given:
            environmentVariables.setProperty("browser.mob.proxy","7777")
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()
        then:
            service.port == 7777
    }

    def "should find the next available port if the initial port is being"() {
        given:
            environmentVariables.setProperty("browser.mob.proxy","8888")
            service = new BrowserMobFixtureService(environmentVariables)
            service.setup()

        and:
            service2 = new BrowserMobFixtureService(environmentVariables)
        when:
            service2.setup()
        then:
            service2.port != 8888
    }

    Ports withNoPorts = new Ports(8888) {
        @Override
        boolean isAvailable(int portNumber) {
            return false
        }
    }

    def "should fail elegantly if no ports are available"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables, withNoPorts)
        when:
            service.setup()
        then:
            thrown(FixtureException)
    }


    Vector<BrowserMobFixtureService> services = []

    def "should cater for several browser mob services in different threads"() {
        given:
            def threads = Thread.start {
                for( i in 1..10 ) {
                    def service =  new BrowserMobFixtureService(environmentVariables)
                    service.setup()
                    services << service
                }
            }
        when:
            threads.join()
        then:
            noExceptionThrown()
    }

    def cleanup() {
        if (service) {
            service.shutdown()
        }
        if (service2) {
            service2.shutdown()
        }
        services.each { it.shutdown() }
    }

}
