package net.serenitybdd.core.reporting;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportDirectories {
    private static final String DEFAULT_REPORT_DIR = "target/site/serenity";

    private final EnvironmentVariables environmentVariables;

    public ReportDirectories() {
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public Path getReportDirectory() {
        return Paths.get(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.optionalFrom(environmentVariables).orElse(DEFAULT_REPORT_DIR));
    }
}
