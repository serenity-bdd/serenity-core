package net.thucydides.model.reports.adaptors;

import net.thucydides.model.domain.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test outcome adaptors provide a way to read test results from an external source.
 * In the most common situation, this data is loaded from a source file or directory,
 * or read from an external provider (e.g. reading manual test results from an external tool).
 *
 * In situations where no source file or directory is required, this parameter can be
 * ignored.
 *
 */
public interface TestOutcomeAdaptor {
    public List<TestOutcome> loadOutcomes() throws IOException;
    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException;
}
