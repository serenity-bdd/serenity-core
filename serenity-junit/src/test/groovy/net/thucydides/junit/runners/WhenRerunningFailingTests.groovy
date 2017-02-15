package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

class WhenRerunningFailingTests extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def webDriverFactory = new WebDriverFactory(environmentVariables)
    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
        environmentVariables.setProperty("rerun.failures.directory",System.getProperty("user.dir") + File.separator + "src/test/resources/rerun")
        environmentVariables.setProperty("replay.failures","true")
    }


    @RunWith(SerenityRunner)
    static class ATestWithMoreTestMethods {

        @Test
        public void testMethod1() {
        }

        @Test
        public void testMethod2() {
        }

        @Test
        public void testMethod3() {
        }
    }

    def "should rerun only tests specified in rerun file"() {
        given:
            def runner = new SerenityRunner(ATestWithMoreTestMethods,webDriverFactory, new SystemPropertiesConfiguration().withEnvironmentVariables(environmentVariables))
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == 2
    }


}
