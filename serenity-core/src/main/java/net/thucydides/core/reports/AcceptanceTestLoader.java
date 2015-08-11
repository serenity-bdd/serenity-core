package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.util.List;

public interface AcceptanceTestLoader {
    Optional<TestOutcome> loadReportFrom(final File reportFile);
    List<TestOutcome> loadReportsFrom(File outputDirectory);
}
