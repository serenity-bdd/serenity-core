package io.cucumber.core.plugin;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ConfigureDriverFromTags {

    public static void forTags(List<String> tags) {
        String requestedDriver = getDriverFrom(tags);
        String requestedDriverOptions = getDriverOptionsFrom(tags);
        if (isNotEmpty(requestedDriver)) {
            ThucydidesWebDriverSupport.useDefaultDriver(requestedDriver);
            ThucydidesWebDriverSupport.useDriverOptions(requestedDriverOptions);
        }
    }

    private static String getDriverFrom(List<String> tags) {
        String requestedDriver = null;
        for (String tag : tags) {
            if (tag.startsWith("@driver:")) {
                requestedDriver = tag.substring(8);
            }
        }
        return requestedDriver;
    }

    private static String getDriverOptionsFrom(List<String> tags) {
        String requestedDriverOptions = "";
        for (String tag : tags) {
            if (tag.startsWith("@driver-options:")) {
                requestedDriverOptions = tag.substring(16);
            }
        }
        return requestedDriverOptions;
    }

    public static void inTheCurrentTestOutcome() {
        if (StepEventBus.getParallelEventBus().isBaseStepListenerRegistered() && StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome() != null) {
            List<String> tags = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome()
                    .getTags().stream()
                    .map(tag -> "@" + tag.toString())
                    .collect(Collectors.toList());

            forTags(tags);
        }
    }
}
