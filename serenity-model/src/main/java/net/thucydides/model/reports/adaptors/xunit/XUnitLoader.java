package net.thucydides.model.reports.adaptors.xunit;

import net.thucydides.model.reports.adaptors.xunit.model.TestSuite;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Converts an xUnit compatible file into a list of TestSuite objects that can be used to create TestOutcomes.
 */
public interface XUnitLoader {
    List<TestSuite> loadFrom(File xUnitReport) throws IOException;
}
