package net.serenitybdd.junit5;

import net.serenitybdd.junit5.datadriven.samples.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnit5DataDrivenAnnotationsTest {


    @Test
    public void testFindValueSourceAnnotatedMethods() {
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(MultipleDataDrivenTestScenariosWithValueSource.class);
        assertEquals(2,dda.findTestDataMethods().size());
    }

    @Test
    public void testCreateColumnNamesFromValueSource() throws Exception {
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(MultipleDataDrivenTestScenariosWithValueSource.class);
        assertEquals("arg0",dda.createColumnNamesFromParameterNames(TestClass.class.getMethod("myMethodWithOneParameter",String.class)));
        assertEquals("arg0,arg1",dda.createColumnNamesFromParameterNames(TestClass.class.getMethod("myMethodWithTwoParameters",String.class,String.class)));
    }

    private class TestClass{
        public void myMethodWithOneParameter(String param1) { }
        public void myMethodWithTwoParameters(String param1,String param2) { }
    }

    @Test
    public void testFindMethodSourceAnnotatedMethods() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithMethodSource.class);
        assertEquals(1,dda.findTestDataMethods().size());
    }

    @Test
    public void testFindMethodSourceAnnotatedMethodsNoName() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithMethodSourceNoName.class);
        assertEquals(1,dda.findTestDataMethods().size());
    }

    @Test
    public void testFindMethodSourceFromExternalSource() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithMethodSourceExternal.class);
        assertEquals(1,dda.findTestDataMethods().size());
    }

    @Test
    public void testFindMethodSourceInstanceMethodSource() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithMethodSourceInstance.class);
        assertEquals(1,dda.findTestDataMethods().size());
    }
}
