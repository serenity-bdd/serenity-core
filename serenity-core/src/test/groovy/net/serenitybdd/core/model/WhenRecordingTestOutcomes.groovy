package net.serenitybdd.core.model
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenRecordingTestOutcomes extends Specification{

    class SomeTestScenario {

    }

    def "should be able to obtain a link to the saucelabs video"() {

        given:
            def environmentVariables = new MockEnvironmentVariables();
            environmentVariables.setProperty("saucelabs.url","http://username:accesskey@ondemand.saucelabs.com:80/wd/hub");
        and:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class);
            testOutcome.setEnvironmentVariables(environmentVariables);
            testOutcome.linkGenerator.environmentVariables = environmentVariables
        when:
            testOutcome.setSessionId("1234");
        then:
            testOutcome.getVideoLink() == "http://saucelabs.com/jobs/1234"

    }

}
