package net.serenitybdd.plugins.jira.workflow

import net.thucydides.core.model.TestResult

class TransitionBuilder extends BuilderSupport {

    def TransitionSetMap transitionSetMap = new TransitionSetMap();
    def currentStatus;

    @Override
    protected void setParent(Object o, Object o1) {
    }

    @Override
    protected Object createNode(Object o) {
        return null
    }

    @Override
    protected Object createNode(Object o, Object status) {
        currentStatus = status
    }

    @Override
    protected Object createNode(Object testOutcome, Map expectations) {
        def expectedTransitions = expectations['should']
        def testResult = TestResult.valueOf(testOutcome.toString().toUpperCase())
        transitionSetMap.forTestResult(testResult).setTransitions(currentStatus, expectedTransitions)
    }

    @Override
    protected Object createNode(Object o, Map map, Object o1) {
        return null
    }

    class TransitionSetMap {

        Map<TestResult, TransitionsForOutcome> transitionsByTestResult = [:]

        TransitionsForOutcome forTestResult(TestResult testResult) {
            if (!transitionsByTestResult[testResult]) {
                transitionsByTestResult[testResult] = new TransitionsForOutcome()
            }
            transitionsByTestResult[testResult]
        }


        String toString ( ) {
            transitionsByTestResult.toMapString()
        }}

    class TransitionsForOutcome {

        Map<String, List<String>> transitionMap = new HashMap<String, List<String>>();

        List<String> whenIssueIs(String status) {
            transitionMap[status] ? transitionMap[status] : []
        }

        void setTransitions(String status, String transition) {
            transitionMap[status] = [transition]
        }

        void setTransitions(String status, List<String> transitions) {
            transitionMap[status] = transitions
        }

        String toString ( ) {
            transitionMap.toMapString()
        }}
}
