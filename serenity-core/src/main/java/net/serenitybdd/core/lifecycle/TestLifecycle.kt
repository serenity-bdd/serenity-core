@file:JvmName("TestLifecycle")
package net.serenitybdd.core.lifecycle

import net.thucydides.core.model.TestOutcome

fun aTestRunHasStartedCalled(name : String) : TestRunStartedEvent {
    return TestRunStartedEvent(name)
}

fun aTestGroupHasStartedCalled(testGroupName : String) : TestGroupStartedEvent {
    return TestGroupStartedEvent(testGroupName)
}

fun aTestHasStartedCalled(name : String) : TestStartedEvent {
    return TestStartedEvent(name)
}

fun aTestHasFinishedWith(testOutcome: TestOutcome) : TestFinishedEvent {
    return TestFinishedEvent(testOutcome)
}

fun aTestGroupHasFinishedWith(testOutcomes: List<TestOutcome>) : TestGroupFinishedEvent {
    return TestGroupFinishedEvent(testOutcomes)
}

fun aTestRunHasFinished(testOutcomes: List<TestOutcome>) : TestRunFinishedEvent {
    return TestRunFinishedEvent(testOutcomes)
}
