package net.thucydides.core.reports;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.IO_BLOCKING_COEFFICIENT;
import static net.thucydides.core.ThucydidesSystemProperty.REPORT_THREADS;

public class NumberOfThreads {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberOfThreads.class);

    static final Double DEFAULT_BLOCKING_COEFFICIENT_FOR_IO = 0.8;

    private final EnvironmentVariables environmentVariables;
    private final double blockingCoefficientForIO;

    public static int forIOOperations() {
        return new NumberOfThreads().forIO();
    }

    protected NumberOfThreads() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    protected NumberOfThreads(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.blockingCoefficientForIO = Double.parseDouble(IO_BLOCKING_COEFFICIENT.from(environmentVariables, DEFAULT_BLOCKING_COEFFICIENT_FOR_IO.toString()));
    }

    public int forIO() {
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        int reportThreads = configuredReportThreads().orElse((int) (numberOfCores / (1 - blockingCoefficientForIO)));
        return reportThreads;
    }

    private Optional<Integer> configuredReportThreads() {
        int reportThreads = REPORT_THREADS.integerFrom(environmentVariables);
        if (reportThreads == 0) { return Optional.empty(); }
        return Optional.of(reportThreads);
    }
}
