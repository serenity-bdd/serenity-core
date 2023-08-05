package net.thucydides.core.webdriver.redimension;

import net.serenitybdd.model.collect.NewMap;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static net.thucydides.core.webdriver.redimension.RedimensionStrategy.*;

/**
 * Created by john on 25/06/2016.
 */
public class RedimensionBrowser {

    private final EnvironmentVariables environmentVariables;

    public RedimensionBrowser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    private Map<RedimensionStrategy, Redimensioner> getRedimensionerStrategies() {
        return NewMap.of(
                DoNotRedimension, new NoopRedimensioner(),
                RedimensionToSpecifiedSize, new ResizeRedimensioner(environmentVariables),
                Maximize, new MaximizeRedimensioner(),
                FullScreen, new FullScreenRedimensioner()
        );
    }

    public void withDriver(final WebDriver driver) {
        RedimensionStrategy strategy = RedimensionStrategy.strategyFor(driver, environmentVariables);
        getRedimensionerStrategies().get(strategy).redimension(driver);
    }
}
