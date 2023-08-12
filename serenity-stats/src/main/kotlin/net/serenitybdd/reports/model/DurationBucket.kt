package net.serenitybdd.reports.model

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import java.time.Duration

class DurationBucket(
    val duration: String,
    val minDuration: Duration,
    val maxDuration: Duration,
    val outcomes: MutableList<TestCaseDuration>
) {
    fun addOutcome(testCaseDuration: TestCaseDuration) {
        if (testCaseDuration.testOutcome.isDataDriven) {
            val outcomeTitle = "${testCaseDuration.testOutcome.title} (${duration})"
            outcomes.add(withExamplesOfMatchingDuration(outcomeTitle, testCaseDuration.testOutcome))
        } else {
            outcomes.add(testCaseDuration)
        }
    }

    private fun withExamplesOfMatchingDuration(title: String, outcome: TestOutcome): TestCaseDuration {
        val outcomeWithMatchingExamples = outcome.copy()
            .removeTopLevelStepsNotMatching { testStep -> this.contains(Duration.ofMillis(testStep.duration)) }
        val outcomeDuration =
            if (outcomeWithMatchingExamples.testSteps.isEmpty()) 0 else outcomeWithMatchingExamples.testSteps[0].duration

        return TestCaseDuration(title, outcomeDuration, outcomeWithMatchingExamples)
    }

    fun contains(duration: Duration) : Boolean = duration.compareTo(minDuration) >= 0 && duration.compareTo(maxDuration) <= 0
    fun getTestOutcomes() = outcomes.map { outcome -> outcome.testOutcome }
    fun getTag(): TestTag = TestTag.withName(duration).andType("Duration")
}
