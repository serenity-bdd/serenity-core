package net.serenitybdd.reports.io

import serenitymodel.net.thucydides.core.reports.OutcomeFormat
import serenitymodel.net.thucydides.core.reports.TestOutcomeLoader
import serenitymodel.net.thucydides.core.reports.TestOutcomes
import java.nio.file.Path

fun testOutcomesIn(outputDirectory: Path): TestOutcomes {
    return TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory.toFile())
}
