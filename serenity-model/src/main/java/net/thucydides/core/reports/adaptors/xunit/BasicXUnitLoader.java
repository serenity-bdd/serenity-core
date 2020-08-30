package net.thucydides.core.reports.adaptors.xunit;

import net.thucydides.core.reports.adaptors.xunit.model.TestCase;
import net.thucydides.core.reports.adaptors.xunit.model.TestException;
import net.thucydides.core.reports.adaptors.xunit.model.TestSuite;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasicXUnitLoader implements XUnitLoader {

    @Override
    public List<TestSuite> loadFrom(final File xUnitReport) throws IOException {

        List<TestSuite> testSuites = new ArrayList<>();
        Document doc = parseDocument(xUnitReport);
        NodeList testSuiteElements = doc.getElementsByTagName("testsuite");
        shouldHaveAtLeastOneTestSuite(testSuiteElements, xUnitReport);
        for (int i = 0; i < testSuiteElements.getLength(); i++) {
            Optional<TestSuite> testSuite = testSuiteFrom(testSuiteElements.item(i));
            testSuite.ifPresent(testSuites::add);
        }
        return testSuites;
    }

    private void shouldHaveAtLeastOneTestSuite(NodeList testSuiteElements, File xUnitReport) {
        if (testSuiteElements.getLength() == 0) {
            throw new CouldNotReadXUnitFileException("Could not read xUnit file " + xUnitReport.getAbsolutePath());
        }
    }

    private Optional<TestSuite> testSuiteFrom(Node testSuiteNode) {
        Element testSuiteElement = (Element) testSuiteNode;
        TestSuite testSuite = TestSuite.named(testSuiteElement.getAttribute("name"));
        List<TestCase> testCases = testCasesFrom(testSuiteElement);
        return Optional.of(testSuite.withTestCases(testCases));
    }

    private List<TestCase> testCasesFrom(Element testSuiteElement) {
        NodeList testCaseElements = testSuiteElement.getElementsByTagName("testcase");
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < testCaseElements.getLength(); i++) {
            testCases.add(testCaseFrom(testCaseElements.item(i)));
        }
        return testCases;
    }

    private TestCase testCaseFrom(Node item) {
        Element testCaseElement = (Element) item;
        TestCase testCase = getBasicTestCaseFrom(testCaseElement);

        Optional<TestException> failure = exceptionElementIn(testCaseElement).ofType("failure");
        if (failure.isPresent()) {
            testCase = testCase.withFailure(failure.get());
        }

        Optional<TestException> error = exceptionElementIn(testCaseElement).ofType("error");
        if (error.isPresent()) {
            testCase = testCase.withError(error.get());
        }

        String skipped = skippedTextIn(testCaseElement);
        if (StringUtils.isNotEmpty(skipped)) {
            testCase = testCase.wasSkipped(skipped);
        }
        return testCase;
    }

    private String skippedTextIn(Element testCaseElement) {
        String skipMessage = "";
        NodeList skipElements = testCaseElement.getElementsByTagName("skipped");
        if (skipElements.getLength() > 0) {
            skipMessage = ((Element) skipElements.item(0)).getAttribute("type");
            skipMessage = StringUtils.isEmpty(skipMessage) ? "unknown" : skipMessage;
        }
        return skipMessage;
    }

    private TestCase getBasicTestCaseFrom(Element testCaseElement) {
        String name = testCaseElement.getAttribute("name");
        String classname = testCaseElement.getAttribute("classname");
        double timeValue = timeFrom(testCaseElement);
        return TestCase.withName(name).andClassname(classname).andTime(timeValue);
    }

    private ExceptionElementBuilder exceptionElementIn(Element testCaseElement) {
        return new ExceptionElementBuilder(testCaseElement);
    }

    private double timeFrom(Element testCaseElement) {
        String time = testCaseElement.getAttribute("time");
        double timeValue = 0.0;
        if (StringUtils.isNotEmpty(time)) {
            timeValue = Double.parseDouble(time);
        }
        return timeValue;
    }

    private Document parseDocument(File xUnitReport) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xUnitReport);
        } catch (Exception e) {
            throw new CouldNotReadXUnitFileException(e.getMessage());
        }
    }

    private static class ExceptionElementBuilder {
        private final Element testCaseElement;

        public ExceptionElementBuilder(Element testCaseElement) {
            this.testCaseElement = testCaseElement;
        }

        public Optional<TestException> ofType(String exceptionType) {
            NodeList failureElements = testCaseElement.getElementsByTagName(exceptionType);
            if (failureElements.getLength() > 0) {
                Element failureElement = (Element) failureElements.item(0);
                String message = failureElement.getAttribute("message");
                String errorOutput = failureElement.getTextContent();
                return Optional.of(new TestException(message, errorOutput, exceptionType));
            }
            return Optional.empty();
        }
    }
}
