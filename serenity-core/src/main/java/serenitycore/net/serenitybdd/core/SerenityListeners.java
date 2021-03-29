package serenitycore.net.serenitybdd.core;

import serenitymodel.net.serenitybdd.core.environment.ConfiguredEnvironment;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitycore.net.thucydides.core.steps.BaseStepListener;
import serenitycore.net.thucydides.core.steps.Listeners;
import serenitycore.net.thucydides.core.steps.StepEventBus;
import serenitymodel.net.thucydides.core.steps.StepListener;
import serenitymodel.net.thucydides.core.webdriver.Configuration;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerenityListeners {
    private Configuration systemConfiguration;
    private BaseStepListener baseStepListener;
    private List<StepListener> stepListeners;

    public SerenityListeners(Configuration systemConfiguration) {
        this(StepEventBus.getEventBus(), systemConfiguration);
    }

    public SerenityListeners(StepEventBus stepEventBus, Configuration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;

        File outputDirectory = getSystemConfiguration().getOutputDirectory();
        baseStepListener = Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
        stepListeners = Arrays.asList(baseStepListener, Listeners.getLoggingListener());

        stepEventBus.dropAllListeners();

        registerListeners(stepEventBus);
    }

    private void registerListeners(StepEventBus stepEventBus) {
        stepEventBus.registerListener(baseStepListener);
        for (StepListener listener : stepListeners) {
            if (listener != null) {
                stepEventBus.registerListener(listener);
            }
        }
    }

    public SerenityListeners withDriver(WebDriver driver) {
        baseStepListener.setDriver(driver);
        return this;
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    public List<TestOutcome> getResults() {
        return new ArrayList<>(baseStepListener.getTestOutcomes());
    }

    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     * @return current configuration object
     */
    protected Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = ConfiguredEnvironment.getConfiguration();
        }
        return systemConfiguration;
    }
}
