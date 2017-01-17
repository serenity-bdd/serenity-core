package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberOfThreads {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberOfThreads.class);

    static final double BLOCKING_COEFFICIENT_FOR_IO = 0.9;

    private final static EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);

    public static int forIOOperations() {

        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        int reportThreads = configuredReportThreads().or((int) (numberOfCores / (1 - BLOCKING_COEFFICIENT_FOR_IO)));
        LOGGER.info("Configured report threads: {}", reportThreads);
        return reportThreads;
    }

    private static Optional<Integer> configuredReportThreads() {
        return Optional.fromNullable(environmentVariables.getPropertyAsInteger("report.threads", null));
    }
}
