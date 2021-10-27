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
        runtime.exitStatus.results

        and:
        runtime.exitStatus.results[0].error.getMessage().contains("this class doesn't have an empty or a page enabled constructor")
    }

    def "should throw a meaningful exception if a step library if the step library could not be instantiated"() {
        given:
        def runtime = CucumberRunner.serenityRunnerForCucumberTestRunner(BrokenStepLibraryScenario.class, outputDirectory);

        when:
        runtime.run();

        then:
        runtime.exitStatus.results

        and:
        runtime.exitStatus.results[0].error.getMessage().contains("Failed to instantiate class")
    }


}