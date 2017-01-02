package net.thucydides.core.webdriver.redimension;

import com.google.common.collect.ImmutableMap;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static net.thucydides.core.webdriver.redimension.RedimensionStrategy.*;

/**
 * Created by john on 25/06/2016.
 */
public class RedimensionBrowser {

    private final EnvironmentVariables environmentVariables;

    public RedimensionBrowser(EnvironmentVariables environmentVariables, Class<? extends WebDriver> driverClass) {
        this.environmentVariables = environmentVariables;
    }

    private Map<RedimensionStrategy, Redimensioner> getRedimensionerStrategies() {
        return ImmutableMap.of(
                DoNotRedimension, new NoopRedimensioner(),
                RedimensionToSpecifiedSize, new ResizeRedimensioner(environmentVariables),
                Maximize, new MaximizeRedimensioner()
        );
    }

    public void withDriver(final WebDriver driver) {
        RedimensionStrategy strategy = RedimensionStrategy.strategyFor(driver, environmentVariables);

        getRedimensionerStrategies().get(strategy).redimension(driver);
    }
}
