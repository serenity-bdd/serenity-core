package net.thucydides.core.reports;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.IO_BLOCKING_COEFFICIENT;
import static net.thucydides.core.ThucydidesSystemProperty.REPORT_THREADS;

public class NumberOfThreads {

    static final Double DEFAULT_BLOCKING_COEFFICIENT_FOR_IO = -1.0;

    private final EnvironmentVariables environmentVariables;
    private final double blockingCoefficientForIO;
    private final ReporterRuntime reporterRuntime;

    public static int forIOOperations() {
        return new NumberOfThreads().forIO();
    }

    protected NumberOfThreads() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class), new SystemReporterRuntime());
    }

    protected NumberOfThreads(EnvironmentVariables environmentVariables) {
        this(environmentVariables, new SystemReporterRuntime());
    }

    protected NumberOfThreads(EnvironmentVariables environmentVariables, ReporterRuntime reporterRuntime) {
        this.environmentVariables = environmentVariables;
        this.blockingCoefficientForIO = Double.parseDouble(IO_BLOCKING_COEFFICIENT.from(environmentVariables, DEFAULT_BLOCKING_COEFFICIENT_FOR_IO.toString()));
        this.reporterRuntime = reporterRuntime;
    }

    public int forIO() {
        final int numberOfCores = reporterRuntime.availableProcessors();
        final int calculatedReportThreads = Math.max(1, (int) ((numberOfCores * 1.0) / (1.0 - blockingCoefficientForIO)));
        return configuredReportThreads().orElse(calculatedReportThreads);
    }

    private Optional<Integer> configuredReportThreads() {
        int reportThreads = REPORT_THREADS.integerFrom(environmentVariables);
        if (reportThreads == 0) { return Optional.empty(); }
        return Optional.of(reportThreads);
    }
}
