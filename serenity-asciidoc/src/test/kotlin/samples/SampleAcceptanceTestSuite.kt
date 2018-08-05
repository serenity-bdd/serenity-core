package samples

import cucumber.api.CucumberOptions
import net.serenitybdd.cucumber.CucumberWithSerenity
import org.junit.runner.RunWith

@RunWith(CucumberWithSerenity::class)
@CucumberOptions(features = arrayOf("src/test/resources/features/complete_scenario_set"))
class SampleAcceptanceTestSuite