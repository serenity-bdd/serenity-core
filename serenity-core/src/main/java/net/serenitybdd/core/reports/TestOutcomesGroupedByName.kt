@file:JvmName("TestOutcomesGroupedByName")
package net.serenitybdd.core.reports

import net.thucydides.core.model.TestOutcome

fun groupedByTestCase(testOutcomes : List<TestOutcome>) : Map<String, List<TestOutcome>> {

   return testOutcomes.fold(emptyMap<String, List<TestOutcome>>(),
                                    {groupedOutcomes, outcome -> mergeTestOutcomes(groupedOutcomes, outcome) })

}

fun mergeTestOutcomes(groupedOutcomes: Map<String, List<TestOutcome>>, outcome: TestOutcome) : Map<String, List<TestOutcome>> {

    val testOutcomeName = testCaseNameOf(outcome)

    val testOutcomesWithNewTestOutcome = groupedOutcomes.get(testOutcomeName).orEmpty().plus(outcome)

    return groupedOutcomes.plus(mapOf(testOutcomeName to testOutcomesWithNewTestOutcome))
}

fun testCaseNameOf(outcome: TestOutcome) : String {
    return outcome.testCaseName ?: outcome.getStoryTitle()
}
