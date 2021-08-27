package net.serenitybdd.junit5;

import net.serenitybdd.junit5.datadriven.samples.SimpleDataDrivenTestScenarioWithValueSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnit5DataDrivenAnnotationsTest {


    @Test
    public void testFindValueSourceAnnotatedMethods() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithValueSource.class);
        assertEquals(2,dda.findTestDataMethods().size());
    }

    @Test
    public void testCreateColumnNamesFrom() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithValueSource.class);
        assertEquals("arg0",dda.createColumnNamesFromParameterNames(TestClass.class.getMethod("myMethodWithOneParameter",String.class)));
        assertEquals("arg0,arg1",dda.createColumnNamesFromParameterNames(TestClass.class.getMethod("myMethodWithTwoParameters",String.class,String.class)));
    }

    private class TestClass{
        public void myMethodWithOneParameter(String param1) { }
        public void myMethodWithTwoParameters(String param1,String param2) { }
    }
}
