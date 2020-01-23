package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.temporal.ChronoUnit

fun maxDurationOf(outcomes: List<TestOutcome>) : Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> maxDurationOf(outcome) }.max()!!
);

fun minDurationOf(outcomes: List<TestOutcome>) : Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> minDurationOf(outcome) }.min()!!
);

fun totalDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> outcome.duration }.sum()
)

fun maxDurationOf(outcome: TestOutcome) =
        if (outcome.isDataDriven && !outcome.testSteps.isEmpty()) {
            outcome.testSteps.map { step -> step.duration }.max()!!
        } else {
            outcome.duration
        }

fun minDurationOf(outcome: TestOutcome) =
        if (outcome.isDataDriven && !outcome.testSteps.isEmpty()) {
            outcome.testSteps.map { step -> step.duration }.min()!!
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

private fun startToFinishTimeIn(outcomes: List<TestOutcome>): Long {
    val minStartTime = outcomes.filter { outcome -> outcome.startTime != null }
            .map { outcome -> outcome.startTime }
            .min()

    val maxEndTime = outcomes.filter { outcome -> outcome.startTime != null }
            .map { outcome -> outcome.endTime }
            .max();

    return if ((minStartTime != null) && (maxEndTime != null))
        ChronoUnit.MILLIS.between(minStartTime, maxEndTime)
    else 0
}

fun averageDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> outcome.duration }.average().toLong()
)

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
