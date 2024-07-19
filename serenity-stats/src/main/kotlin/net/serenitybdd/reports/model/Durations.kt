package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun maxDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
    if (outcomes.isEmpty()) 0 else outcomes.maxOf { outcome -> maxDurationOf(outcome)!! }
)

fun minDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
    if (outcomes.isEmpty()) 0 else outcomes.minOf { outcome -> minDurationOf(outcome)!! }
)

fun totalDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
    if (outcomes.isEmpty()) 0 else outcomes.sumOf { outcome -> outcome.duration }
)

fun maxDurationOf(outcome: TestOutcome) =
    if (outcome.isDataDriven && outcome.testSteps.isNotEmpty()) {
        outcome.testSteps.maxOfOrNull { step -> step.duration }
    } else {
        outcome.duration
    }

fun minDurationOf(outcome: TestOutcome) =
    if (outcome.isDataDriven && outcome.testSteps.isNotEmpty()) {
        outcome.testSteps.minOfOrNull { step -> step.duration }
    } else {
        outcome.duration
    }

fun clockDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
    if (outcomes.isEmpty())
        0
    else {
        startToFinishTimeIn(outcomes)
    }
)

fun startTimeOf(outcomes: List<TestOutcome>): ZonedDateTime? =
    outcomes.filter { outcome -> outcome.startTime != null }
        .minOfOrNull { outcome -> outcome.startTime }

fun endTimeOf(outcomes: List<TestOutcome>): ZonedDateTime? =
    outcomes.filter { outcome -> outcome.endTime != null }
        .maxOfOrNull { outcome -> outcome.endTime }

private fun startToFinishTimeIn(outcomes: List<TestOutcome>): Long {
    val minStartTime = startTimeOf(outcomes)
    val maxEndTime = endTimeOf(outcomes)

    return if ((minStartTime != null) && (maxEndTime != null))
        ChronoUnit.MILLIS.between(minStartTime, maxEndTime)
    else 0
}

fun averageDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
    if (outcomes.isEmpty())
        0
    else
        outcomes.flatMap { outcome -> testCaseDurationsIn(outcome) }
            .map { duration -> duration.toMillis() }
            .average()
            .toLong()
)

fun testCaseDurationsIn(outcome: TestOutcome): List<Duration> =
    if (outcome.isDataDriven)
        outcome.testSteps.map { step -> ofMillis(step.duration)
    } else
        listOf(ofMillis(outcome.duration))


fun formattedDuration(duration: Duration): String {
    val days = duration.toDays()
    val hours = duration.toHours() - (days * 24)
    val minutes = duration.toMinutes() - (days * 24 * 60) - (hours * 60)
    val seconds = duration.seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - minutes * 60
    val durationInMilliseconds = "" + duration.toMillis() + "ms"

    val durationForSlowTests =
        (if (days > 0) "" + days + "d " else "") +
                (if (hours > 0) " " + hours + "h " else "") +
                (if (minutes > 0) " " + minutes + "m " else "") +
                (if (seconds > 0) " " + seconds + "s " else "0s ").trim()

    return if (duration.toMillis() < 1000L) durationInMilliseconds else durationForSlowTests
}
