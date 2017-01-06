package serenity.example;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.Assert;

/**
 * User: YamStranger
 * Date: 11/7/15
 * Time: 5:08 PM
 */
class AuthorizationSteps {
    private int failureCount;
    private int tries = 1 + 2;//1 base + 2 retries

    @Step
    public void checkRetriesValue() {
        Assert.assertTrue(
            SystemPropertiesConfiguration.MAX_RETRIES + " should be more than 1 " +
                "for this tests",
            new SystemEnvironmentVariables().getPropertyAsInteger(
                SystemPropertiesConfiguration.MAX_RETRIES, 0) > 1);
    }

    @Step
    public void authorizeByToken(final String token) {
        this.failureCount += 1;
        if (this.failureCount <= tries) {
            Assert.fail("should fail for " + this.failureCount + " step");
        }
    }
}
