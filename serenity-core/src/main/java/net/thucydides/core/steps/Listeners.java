package net.thucydides.core.steps;

import com.google.inject.Key;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.statistics.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Listeners {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listeners.class);

    public static BaseStepListenerBuilder getBaseStepListener() {
        return new BaseStepListenerBuilder();
    }

    public static class BaseStepListenerBuilder {

        public BaseStepListenerBuilder and() {
            return this;
        }

        public BaseStepListener withOutputDirectory(File outputDirectory) {
            return new BaseStepListener(outputDirectory);
        }
    }

    public static StepListener getLoggingListener() {
        return Injectors.getInjector().getInstance(Key.get(StepListener.class, ThucydidesLogging.class));
    }

    public static StepListener getStatisticsListener() {
        try {
            return Injectors.getInjector().getInstance(Key.get(StepListener.class, Statistics.class));
        } catch (Throwable statisticsListenerException) {
            LOGGER.error("Failed to create the statistics listener - possible database configuration issue", statisticsListenerException);
        }
        return null;
    }
}
