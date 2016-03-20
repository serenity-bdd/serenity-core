@file:JvmName("TestLifecycle")
package net.serenitybdd.core.lifecycle

import net.thucydides.core.model.TestOutcome

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
