package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.temporal.ChronoUnit

fun maxDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> outcome.duration }.max()!!
)

fun minDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> outcome.duration }.min()!!
)


fun totalDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty()) 0 else outcomes.map { outcome -> outcome.duration }.sum()
)

fun clockDurationOf(outcomes: List<TestOutcome>): Duration = ofMillis(
        if (outcomes.isEmpty())
            0
        else {
            val minStartTime = outcomes.filter { outcome -> outcome.startTime != null }
                                       .map { outcome -> outcome.startTime }
                                       .min()

            val maxEndTime = outcomes.filter { outcome -> outcome.startTime != null }
                    .map { outcome -> outcome.endTime }
                    .max();

            ChronoUnit.MILLIS.between(minStartTime, maxEndTime)
        }
)

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
