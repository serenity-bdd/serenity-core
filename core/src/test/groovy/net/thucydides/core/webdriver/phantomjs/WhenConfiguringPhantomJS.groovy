package net.thucydides.core.webdriver.phantomjs

import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Specification


class WhenConfiguringPhantomJS extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def capabilities = Mock(DesiredCapabilities)

    def "should set basic proxy config"() {
        given:
            environmentVariables.setProperty("thucydides.proxy.http","http://proxy.mycompany.com")
            def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
            enhancer.enhanceCapabilities(capabilities)
        then:
            1 * capabilities.setCapability("phantomjs.cli.args",
                                           ['--web-security=false',
                                            '--ssl-protocol=any',
                                            '--ignore-ssl-errors=true',
                                            '--webdriver-loglevel=OFF',
                                            '--proxy=http://proxy.mycompany.com'])

    }

    def "should set proxy config with port"() {
        given:
            environmentVariables.setProperty("thucydides.proxy.http","http://proxy.mycompany.com")
            environmentVariables.setProperty("thucydides.proxy.http_port","8080")
            def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
            enhancer.enhanceCapabilities(capabilities)
        then:
            1 * capabilities.setCapability("phantomjs.cli.args",
                    ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver-loglevel=OFF',
                        '--proxy=http://proxy.mycompany.com:8080'])

    }

    def "should set proxy config with username and password"() {
        given:
            environmentVariables.setProperty("thucydides.proxy.http","http://proxy.mycompany.com")
            environmentVariables.setProperty("thucydides.proxy.http_port","8080")
            environmentVariables.setProperty("thucydides.proxy.user","scott")
            environmentVariables.setProperty("thucydides.proxy.password","tiger")
            def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
         enhancer.enhanceCapabilities(capabilities)
        then:
            1 * capabilities.setCapability("phantomjs.cli.args",
                    ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver-loglevel=OFF',
                        '--proxy=http://proxy.mycompany.com:8080',
                        '--proxy-auth=scott:tiger'])
    }

    def "should set proxy config with type"() {
        given:
        environmentVariables.setProperty("thucydides.proxy.http","http://proxy.mycompany.com")
        environmentVariables.setProperty("thucydides.proxy.http_port","8080")
        environmentVariables.setProperty("thucydides.proxy.type","socks5")
        def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
        enhancer.enhanceCapabilities(capabilities)
        then:
        1 * capabilities.setCapability("phantomjs.cli.args",
                ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver-loglevel=OFF',
                        '--proxy=http://proxy.mycompany.com:8080',
                        '--proxy-type=socks5'])
    }

    def "should configure for selenium hub"() {
        given:
        environmentVariables.setProperty("webdriver.remote.url","http://127.0.0.1:4444")
        environmentVariables.setProperty("phantomjs.webdriver.port","5555")

        def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
        enhancer.enhanceCapabilities(capabilities)
        then:
        1 * capabilities.setCapability("phantomjs.cli.args",
                ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver-loglevel=OFF',
                        '--webdriver-selenium-grid-hub=http://127.0.0.1:4444',
                        '--webdriver=5555'])
    }

    def "should configure for selenium hub without webdriver port"() {
        given:
        environmentVariables.setProperty("webdriver.remote.url","http://127.0.0.1:4444")

        def enhancer = new PhantomJSCapabilityEnhancer(environmentVariables)
        when:
        enhancer.enhanceCapabilities(capabilities)
        then:
        1 * capabilities.setCapability("phantomjs.cli.args",
                ['--web-security=false',
                        '--ssl-protocol=any',
                        '--ignore-ssl-errors=true',
                        '--webdriver-loglevel=OFF',
                        '--webdriver-selenium-grid-hub=http://127.0.0.1:4444'])
    }
}