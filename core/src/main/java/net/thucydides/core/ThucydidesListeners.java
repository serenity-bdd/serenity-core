package net.thucydides.core;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.webdriver.Configuration;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.List;

public class ThucydidesListeners {
    private Configuration systemConfiguration;
    private BaseStepListener baseStepListener;
    private List<StepListener> stepListeners;

    public ThucydidesListeners(Configuration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;

        File outputDirectory = getSystemConfiguration().getOutputDirectory();
        baseStepListener = Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
        stepListeners = ImmutableList.of(baseStepListener,
                                         Listeners.getLoggingListener());
                                         //Listeners.getStatisticsListener());

        StepEventBus.getEventBus().dropAllListeners();
        registerListeners();
    }

    private void registerListeners() {
        StepEventBus.getEventBus().registerListener(baseStepListener);
        for (StepListener listener : stepListeners) {
            if (listener != null) {
                StepEventBus.getEventBus().registerListener(listener);
            }
        }
    }

    public ThucydidesListeners withDriver(WebDriver driver) {
        baseStepListener.setDriver(driver);
        return this;
    }

    public BaseStepListener getBaseStepListener() {
        return baseStepListener;
    }

    public List<TestOutcome> getResults() {
        return ImmutableList.copyOf(baseStepListener.getTestOutcomes());
    }

    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     * @return current configuration object
     */
    protected Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = Injectors.getInjector().getInstance(Configuration.class);
        }
        return systemConfiguration;
    }
}
