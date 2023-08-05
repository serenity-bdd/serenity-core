package net.serenitybdd.reports.io

import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import net.thucydides.model.reports.TestOutcomes
import java.nio.file.Path

fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
    return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
}
