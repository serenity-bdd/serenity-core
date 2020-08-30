package net.serenitybdd.reports.io

import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.reports.TestOutcomes
import java.nio.file.Path

fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
    return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
}
