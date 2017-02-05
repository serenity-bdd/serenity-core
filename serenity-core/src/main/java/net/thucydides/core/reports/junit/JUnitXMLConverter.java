package net.thucydides.core.reports.junit;

import net.thucydides.core.model.*;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.reports.TestOutcomes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public class JUnitXMLConverter {


    public void write(String testCaseName, List<TestOutcome> outcomes, OutputStream outputStream) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        TestOutcomes testCaseOutcomes = TestOutcomes.of(outcomes);

        // root elements
        Document doc = docBuilder.newDocument();

        Element testSuiteElement = buildTestSuiteElement(doc, testCaseName, testCaseOutcomes);

        for(TestOutcome outcome : outcomes) {
            Element testCaseElement = buildTestCaseElement(doc, outcome);
            testSuiteElement.appendChild(testCaseElement);
        }
        doc.appendChild(testSuiteElement);

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outputStream);

        transformer.transform(source, result);
    }

    private Element buildTestCaseElement(Document doc, TestOutcome outcome) {
        Element testCaseElement = doc.createElement("testcase");

        if (outcome.getTestCase() != null) {
            testCaseElement.setAttribute("classname", outcome.getTestCase().getName());
        }
        if (outcome.getTitle() != null) {
            testCaseElement.setAttribute("name", outcome.getTitle());
        }

        if (outcome.isFailure()) {
            addFailureElement(doc, outcome, testCaseElement);
        } else if (outcome.isError()) {
            addErrorElement(doc, outcome, testCaseElement);
        } else if (outcome.isCompromised()) {
            addCompromisedElement(doc, outcome, testCaseElement);
        } else  if (outcome.isSkipped() || outcome.isPending()) {
            testCaseElement.appendChild(doc.createElement("skipped"));
        } else {
            String flakyTestDescription = getFlakyTestDescription(outcome);
            if (flakyTestDescription != null) {
                addFlakyFailureElement(doc, outcome, testCaseElement, flakyTestDescription);
            }
        }
        return testCaseElement;

    }

    private String getFlakyTestDescription(TestOutcome outcome) {
        Set<TestTag> tags = outcome.getTags();
        for(TestTag tag : tags) {
            if(tag.getType().equals("unstable test")) {
                List<TestStep> testSteps = outcome.getTestSteps();
                for(TestStep step: testSteps) {
                    if(step.getDescription().startsWith("UNSTABLE TEST:")) {
                        return step.getDescription();
                    }
                }
            }
        }
        return null;
    }

    private void addErrorElement(Document doc, TestOutcome outcome, Element testCaseElement) {
        testCaseElement.appendChild(errorElement(doc, outcome));
        if (outcome.getNestedTestFailureCause() != null) {
            testCaseElement.appendChild(syserrorElement(doc, outcome.getNestedTestFailureCause()));
        }
    }

    private void addCompromisedElement(Document doc, TestOutcome outcome, Element testCaseElement) {
        testCaseElement.appendChild(compromisedElement(doc, outcome));
        if (outcome.getNestedTestFailureCause() != null) {
            testCaseElement.appendChild(syserrorElement(doc, outcome.getNestedTestFailureCause()));
        }
    }

    private void addFailureElement(Document doc, TestOutcome outcome, Element testCaseElement) {
        testCaseElement.appendChild(failureElement(doc, outcome));
        if (outcome.getNestedTestFailureCause() != null) {
            testCaseElement.appendChild(syserrorElement(doc, outcome.getNestedTestFailureCause()));
        }
    }

    private void addFlakyFailureElement(Document doc, TestOutcome outcome, Element testCaseElement,String flakyTestDescription) {
        Node flakyFailureElement = testCaseElement.appendChild(flakyFailureElement(doc, outcome, flakyTestDescription));
        if (outcome.getFlakyTestFailureCause() != null) {
            flakyFailureElement.appendChild(syserrorElement(doc, outcome.getFlakyTestFailureCause().getRootCause()));
        }
    }

    private Element syserrorElement(Document doc, FailureCause nestedTestFailureCause) {
        Element testCaseElement = doc.createElement("system-err");

        StringBuilder printedStackTrace = new StringBuilder();
        printedStackTrace.append(nestedTestFailureCause.getMessage());
        printedStackTrace.append(System.lineSeparator());
        for(StackTraceElement element : nestedTestFailureCause.getStackTrace()) {
            printedStackTrace.append(element.toString());
            printedStackTrace.append(System.lineSeparator());
        }
        testCaseElement.appendChild(doc.createTextNode(printedStackTrace.toString()));
        return testCaseElement;
    }

    private Element failureElement(Document doc, TestOutcome outcome) {
        Element testCaseElement = doc.createElement("failure");
        addFailureCause(doc, testCaseElement, outcome.getNestedTestFailureCause());
        return testCaseElement;
    }

    private Element flakyFailureElement(Document doc, TestOutcome outcome,String flakyTestDescription) {
        Element testCaseElement = doc.createElement("flakyFailure");
        addFlakyFailureCause(doc, testCaseElement, outcome.getFlakyTestFailureCause().getRootCause(),flakyTestDescription);
        return testCaseElement;
    }

    private void addFailureCause(Document doc, Element testCaseElement, FailureCause failureCause) {
        if ((failureCause != null) && (failureCause.getMessage() != null)) {
            testCaseElement.setAttribute("message", failureCause.getMessage());
            testCaseElement.appendChild(doc.createTextNode(failureCause.getMessage()));
        }
        if ((failureCause != null) && (failureCause.getErrorType() != null)) {
            testCaseElement.setAttribute("type", failureCause.getErrorType());
        }
    }

    private void addFlakyFailureCause(Document doc, Element testCaseElement, FailureCause failureCause,String failureCauseDescription) {
        if ((failureCause != null) && (failureCause.getMessage() != null)) {
            testCaseElement.setAttribute("message", failureCause.getMessage());
            testCaseElement.appendChild(doc.createTextNode(failureCauseDescription));
        }
        if ((failureCause != null) && (failureCause.getErrorType() != null)) {
            testCaseElement.setAttribute("type", failureCause.getErrorType());
        }
    }

    private Element errorElement(Document doc, TestOutcome outcome) {
        Element testCaseElement = doc.createElement("error");
        addFailureCause(doc, testCaseElement, outcome.getNestedTestFailureCause());
        return testCaseElement;
    }

    private Element compromisedElement(Document doc, TestOutcome outcome) {
        Element testCaseElement = doc.createElement("compromised");
        addFailureCause(doc, testCaseElement, outcome.getNestedTestFailureCause());
        return testCaseElement;
    }

    private Element buildTestSuiteElement(Document doc, String testCaseName, TestOutcomes testCaseOutcomes) {
        int errors = testCaseOutcomes.count(TestType.ANY).withResult(TestResult.ERROR);
        int failures = testCaseOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE);
        int success = testCaseOutcomes.count(TestType.ANY).withResult(TestResult.SUCCESS);
        int skipped = testCaseOutcomes.getTotal() - errors - failures - success;

        Element testSuiteElement = doc.createElement("testsuite");
        testSuiteElement.setAttribute("name", testCaseName);
        testSuiteElement.setAttribute("time", Double.toString(testCaseOutcomes.getDurationInSeconds()));
        testSuiteElement.setAttribute("tests", Integer.toString(testCaseOutcomes.getTestCount()));
        testSuiteElement.setAttribute("errors", Integer.toString(errors));
        testSuiteElement.setAttribute("skipped", Integer.toString(skipped));
        testSuiteElement.setAttribute("failures", Integer.toString(failures));
        if (testCaseOutcomes.getStartTime() != null) {
            testSuiteElement.setAttribute("timestamp", testCaseOutcomes.getStartTime().toString("YYYY-MM-DD hh:mm:ss"));
        }
        return testSuiteElement;
    }
}
