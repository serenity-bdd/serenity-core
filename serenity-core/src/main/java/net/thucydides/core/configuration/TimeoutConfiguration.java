package net.thucydides.core.configuration;

import com.google.common.base.Splitter;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TimeoutConfiguration {
    public static TimeoutConfigurationGetter from(EnvironmentVariables environmentVariables) {
        return new TimeoutConfigurationGetter(environmentVariables);
    }

    public static class TimeoutConfigurationGetter {
        private final EnvironmentVariables environmentVariables;

        public TimeoutConfigurationGetter(EnvironmentVariables environmentVariables) {

            this.environmentVariables = environmentVariables;
        }

        public TimeoutValue forProperty(String configurationProperty, TimeoutValue defaultTimeout) {
            String timeout = environmentVariables.getProperty(configurationProperty);
            if (isEmpty(timeout)) {
                return defaultTimeout;
            }
            List<String> timeoutElements = Splitter.on(" ").splitToList(timeout);

            long numericalValue = Long.parseLong(timeoutElements.get(0));
            TimeUnit timeoutUnits = (timeoutElements.size() > 1) ? TimeUnit.valueOf(timeoutElements.get(1).toUpperCase()) : TimeUnit.SECONDS;

            return new TimeoutValue(numericalValue, timeoutUnits);
        }
    }
}
