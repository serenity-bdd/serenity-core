package net.serenitybdd.core;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.webdriver.Configuration;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerenityListeners {
    private Configuration systemConfiguration;
    private final BaseStepListener baseStepListener;
    private final List<StepListener> stepListeners;

    public SerenityListeners(Configuration systemConfiguration) {
        this(StepEventBus.getParallelEventBus(), systemConfiguration);
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
