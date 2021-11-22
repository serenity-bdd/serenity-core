package net.serenitybdd.reports.model

import jnr.constants.platform.Local
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class DurationDistribution(
    val environmentVariables: EnvironmentVariables,
    val testOutcomes: TestOutcomes
) {

    companion object {
        const val DEFAULT_DURATION_RANGES_IN_SECONDS = "1, 10, 30, 60, 120, 300, 600";
    }

    val durationLimits = durationLimitsDefinedIn(environmentVariables)
    val durationBuckets = calculateDurationBuckets()

    fun getNumberOfTestsPerDuration(): String {
        val durationCounts = durationBuckets.map { bucket -> numberOfScenariosIn(bucket.outcomes) }
        return asFormattedList(durationCounts)
    }

    private fun numberOfScenariosIn(outcomes: MutableList<TestOutcome>) : Int {
        return outcomes.map { outcome -> outcome.testCount}.sum()
    }

    private fun calculateDurationBuckets(): List<DurationBucket> {

        val durationBuckets: MutableList<DurationBucket> = mutableListOf()
        val bucketLabels = distributionLabels()
        for((index, durationLimit)  in durationLimits.plus(Duration.ofSeconds(Long.MAX_VALUE)).withIndex()) {
            durationBuckets.add(
                DurationBucket(
                    bucketLabels.get(index),
                    durationLimit.get(ChronoUnit.SECONDS),
                    mutableListOf()
                )
            )
        }

        for (testOutcome in testOutcomes.tests) {
            val bucket = bucketFor(testOutcome.durationInSeconds.toLong())
            durationBuckets.get(bucket).addOutcome(testOutcome)
        }
        return durationBuckets
    }

    private fun bucketFor(duration: Long): Int {
        var bucketNumber = 0;

        for (durationLimit in durationLimits) {
            if (duration < durationLimit.seconds) {
                return bucketNumber
            }
            bucketNumber++
        }
        return bucketNumber
    }

    fun distributionLabels(): List<String> {

        val labels: MutableList<String> = mutableListOf()

        labels.add("Under ${formatted(durationLimits.first())}")
        for( i in 0..durationLimits.size - 2) {
            val lowerLimit = durationLimits.get(i)
            val upperLimit = durationLimits.get(i + 1)
            if (unitOf(lowerLimit) == unitOf(upperLimit)) {
                // 1 to 2 seconds
                labels.add("${valueOf(lowerLimit, unitOf(lowerLimit))} to ${formatted(upperLimit)}")
            } else if ((unitOf(lowerLimit) != unitOf(upperLimit)) && (valueOf(upperLimit, unitOf(upperLimit)) == "1")) {
                // 30 seconds to 1 minute -> 30 to 60 seconds
                labels.add("${valueOf(lowerLimit, unitOf(lowerLimit))} to ${formattedWithUnit(upperLimit, unitOf(lowerLimit))}")
            } else {
                // 30 seconds to 1 minute 30 seconds
                labels.add("${formatted(lowerLimit)} to ${formatted(upperLimit)}")
            }
        }
        labels.add("${formatted(durationLimits.last())} or over")

        return labels
    }

    fun getBucketLabels(): String {
        return asFormattedList(distributionLabels())
    }

    fun getBucketCount(): Int {
        return distributionLabels().size
    }

    fun asFormattedList(labels: List<Any>) = "[${labels.map { duration -> "'${duration}'" }.joinToString(",")}]"

    fun unitOf(duration: Duration): String {
        if (toHoursPart(duration)  > 0) return "HOURS";
        if (toMinutesPart(duration) > 0) return "MINUTES";
        if (duration.seconds > 0) return "SECONDS";
        return "MILLISECONDS"
    }

    fun formattedWithUnit(duration: Duration, unit: String): String {
        return when(unit) {
            "SECONDS" ->  seconds(duration.get(ChronoUnit.SECONDS)).trim()
            "MINUTES" -> minutes(duration.get(ChronoUnit.MINUTES)).trim()
            "HOURS" -> hours(duration.get(ChronoUnit.HOURS)).trim()
            else -> "${duration.toMillis()} ms"
        }
    }

    fun valueOf(duration: Duration, unit: String): String {
        return when(unit) {
            "SECONDS" ->  duration.seconds.toString()
            "MINUTES" -> (duration.seconds / 60).toString()
            "HOURS" -> (duration.seconds / 3600).toString()
            else -> duration.toMillis().toString()
        }
    }

    fun formatted(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = toMinutesPart(duration)
        val seconds = toSecondsPart(duration);
        return "${hours(hours)} ${minutes(minutes.toLong())} ${seconds(seconds.toLong())}".trim()
    }

    fun seconds(value: Long): String {
        if (value == 0L) return "";
        return if (value == 1L) "${value} second" else "${value} seconds"
    }

    fun minutes(value: Long): String {
        if (value == 0L) return "";
        return if (value == 1L) "${value} minute" else "${value} minutes"
    }

    fun hours(value: Long): String {
        if (value == 0L) return "";
        return if (value == 1L) "${value} hour" else "${value} hours"
    }

    fun toHoursPart(duration: Duration): Int {
        return (duration.toHours() % 24).toInt();
    }

    fun toMinutesPart(duration: Duration): Int {
        return (duration.toMinutes() % 60).toInt();
    }

    fun toSecondsPart(duration: Duration): Int {
        return (duration.seconds % 60).toInt();
    }

    private fun durationLimitsDefinedIn(environmentVariables: EnvironmentVariables): List<Duration> {
        var durationLimits = EnvironmentSpecificConfiguration.from(environmentVariables)
            .getOptionalProperty(ThucydidesSystemProperty.SERENITY_REPORT_DURATIONS)
            .orElse(DEFAULT_DURATION_RANGES_IN_SECONDS)

        return durationLimits.split(",").map { value ->
            Duration.ofSeconds(Integer.parseInt(value.trim()).toLong())
        }
    }

    fun bucketFor(testOutcome: TestOutcome): DurationBucket {
        for (bucket in durationBuckets) {
            if (testOutcome.durationInSeconds < bucket.durationInSeconds) {
                return bucket;
            }
        }
        return durationBuckets.last()
    }

    fun getDurationTags() : List<TestTag> = durationBuckets.map { bucket -> bucket.getTag() }

}