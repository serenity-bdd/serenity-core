package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface AcceptanceTestLoader {
    java.util.Optional<TestOutcome> loadReportFrom(final File reportFile);

    List<TestOutcome> loadReportsFrom(final File outputDirectory);

    java.util.Optional<TestOutcome> loadReportFrom(final Path reportFile);

    List<TestOutcome> loadReportsFrom(final Path outputDirectory);

    Optional<OutcomeFormat> getFormat();
}
