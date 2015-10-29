package serenity.example;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * User: YamStranger
 * Date: 10/26/2015
 * Time: 1:14 PM
 *
 * Class contains example of test case.
 */
@RunWith(SerenityRunner.class)
public class WhenAuthorisationPerforming {

    @Steps
    serenity.example.AuthorizationSteps steps;

    @Test
    public void successfulAuthorisation() {
        this.steps.authorizeByToken("correct");
    }
}