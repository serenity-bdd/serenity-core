package net.thucydides.core.reports;

import java.nio.file.Path;

public interface ExtendedReport {
    String getName();
    String getDescription();
    void setSourceDirectory(Path sourceDirectory);
    void setOutputDirectory(Path outputDirectory);
    Path generateReport();
}
