package net.serenitybdd.junit5;

import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

public class SerenityTestExecutionListenerTest {

    @Test
    void testGetTestTemplateInvocationNumber(){
        assertEquals(0, SerenityTestExecutionListener.getTestTemplateInvocationNumber("[engine:junit-jupiter]/[class:starter.examples.SimpleEnumSourceDataDrivenTest]/[test-template:withEnumSource(starter.domain.Books)]/[test-template-invocation:#1]"));
        assertEquals(11, SerenityTestExecutionListener.getTestTemplateInvocationNumber("[engine:junit-jupiter]/[class:starter.examples.SimpleEnumSourceDataDrivenTest]/[test-template:withEnumSource(starter.domain.Books)]/[test-template-invocation:#12]"));
    }
}
