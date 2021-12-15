package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag

class DurationBucket(val duration: String,
                     val durationInSeconds : Long,
                     val outcomes: MutableList<TestOutcome>) {
    fun addOutcome(testOutcome: TestOutcome) {
        outcomes.add(testOutcome)
    }

    fun getTag() : TestTag = TestTag.withName(duration).andType("Duration")
}