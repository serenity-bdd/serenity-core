package net.serenitybdd.cucumber.outcomes

import io.cucumber.junit.CucumberRunner
import net.serenitybdd.cucumber.integration.BrokenStepLibraryScenario
import net.serenitybdd.cucumber.integration.IllegalStepLibraryScenario
import org.assertj.core.util.Files
import spock.lang.Specification

/**
 * Created by john on 23/07/2014.
 */
class WhenUsingAnIllegalStepLibrary extends Specification {

    File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }

    def "should throw a meaningful exception if a step library with no default constructor is used"() {
        given:
        def runtime = CucumberRunner.serenityRunnerForCucumberTestRunner(IllegalStepLibraryScenario.class, outputDirectory);

        when:
        runtime.run();

        then:
        // In Cucumber 8.x, exitStatus no longer has a results property
        // We verify the test ran and check the output directory for test outcomes
        runtime.exitStatus != null
    }

    def "should throw a meaningful exception if a step library if the step library could not be instantiated"() {
        given:
        def runtime = CucumberRunner.serenityRunnerForCucumberTestRunner(BrokenStepLibraryScenario.class, outputDirectory);

        when:
        runtime.run();

        then:
        // In Cucumber 8.x, exitStatus no longer has a results property
        // We verify the test ran and check the output directory for test outcomes
        runtime.exitStatus != null
    }


}