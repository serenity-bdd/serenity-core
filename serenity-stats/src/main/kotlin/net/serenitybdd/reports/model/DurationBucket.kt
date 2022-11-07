package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag

class DurationBucket(
    val duration: String,
    val minDurationInSeconds: Long,
    val maxDurationInSeconds: Long,
    val outcomes: MutableList<TestCaseDuration>
) {
    private val minDurationInMilliseconds = minDurationInSeconds * 1000L
    private val maxDurationInMilliseconds = maxDurationInSeconds * 1000L

    fun addOutcome(testCaseDuration: TestCaseDuration) {
        if (testCaseDuration.testOutcome.isDataDriven) {
            val outcomeTitle = "${testCaseDuration.testOutcome.getTitle()} (${duration})"
            outcomes.add(withExamplesOfMatchingDuration(outcomeTitle, testCaseDuration.testOutcome))
        } else {
            outcomes.add(testCaseDuration)
        }
    }

    private fun withExamplesOfMatchingDuration(title: String, outcome: TestOutcome): TestCaseDuration {
        val outcomeWithMatchingExamples = outcome.copy()
            .removeTopLevelStepsNotMatching { testStep ->
                testStep.duration in minDurationInMilliseconds until maxDurationInMilliseconds
            };

        return TestCaseDuration(title, outcomeWithMatchingExamples.testSteps[0].duration, outcomeWithMatchingExamples)
    }

    fun getTestOutcomes() = outcomes.map { outcome -> outcome.testOutcome }
    fun getTag(): TestTag = TestTag.withName(duration).andType("Duration")


}
