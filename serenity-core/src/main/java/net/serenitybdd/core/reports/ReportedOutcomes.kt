package net.serenitybdd.core.reports

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

fun workingCopyOf(reportName: String) = reportName + UUID.randomUUID().toString()

fun copyWorkingCopyToTarget(workingCopy: Path, targetReport: Path) {
    Files.move(workingCopy, targetReport,
            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
}
