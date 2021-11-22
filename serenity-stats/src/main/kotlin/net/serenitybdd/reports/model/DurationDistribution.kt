package net.serenitybdd.reports.model

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.EnvironmentVariables
import java.time.Duration

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
                    durationLimit.toSeconds(),
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
        if (duration.toHours() > 0) return "HOURS";
        if (duration.toMinutes() > 0) return "MINUTES";
        if (duration.toSeconds() > 0) return "SECONDS";
        return "MILLISECONDS"
    }

    fun formattedWithUnit(duration: Duration, unit: String): String {
        return when(unit) {
            "SECONDS" ->  seconds(duration.toSeconds()).trim()
            "MINUTES" -> minutes(duration.toMinutes()).trim()
            "HOURS" -> hours(duration.toHours()).trim()
            else -> "${duration.toMillis()} ms"
        }
    }

    fun valueOf(duration: Duration, unit: String): String {
        return when(unit) {
            "SECONDS" ->  duration.toSeconds().toString()
            "MINUTES" -> duration.toMinutes().toString()
            "HOURS" -> duration.toHours().toString()
            else -> duration.toMillis().toString()
        }
    }

    fun formatted(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart();
        return "${hours(hours)} ${minutes(minutes)} ${seconds(seconds)}".trim()
    }

    fun seconds(value: Number): String {
        if (value == 0) return "";
        return if (value == 1) "${value} second" else "${value} seconds"
    }

    fun minutes(value: Number): String {
        if (value == 0) return "";
        return if (value == 1) "${value} minute" else "${value} minutes"
    }

    fun hours(value: Number): String {
        if (value == 0L) return "";
        return if (value == 1L) "${value} hour" else "${value} hours"
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