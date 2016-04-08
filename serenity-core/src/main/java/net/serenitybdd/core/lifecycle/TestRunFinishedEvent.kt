package net.serenitybdd.core.lifecycle

import net.thucydides.core.model.TestOutcome

class TestRunFinishedEvent(val testOutcomes : List<TestOutcome>)
