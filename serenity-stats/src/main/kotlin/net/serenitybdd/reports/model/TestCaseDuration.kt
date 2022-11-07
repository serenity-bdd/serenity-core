package net.serenitybdd.reports.model

import net.thucydides.core.model.TestOutcome

class TestCaseDuration(val title: String,
                       val duration: Long,
                       val testOutcome: TestOutcome) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestCaseDuration

        if (title != other.title) return false
        if (testOutcome != other.testOutcome) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + testOutcome.hashCode()
        return result
    }

}
