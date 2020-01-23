package net.thucydides.core.reports;

import java.io.File;
import java.nio.file.Path;

public interface ExtendedReport {
    String getName();
    String getDescription();
    File generateReportFrom(Path sourceDirectory);
}
