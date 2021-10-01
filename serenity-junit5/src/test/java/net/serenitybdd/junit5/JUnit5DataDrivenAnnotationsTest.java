package net.serenitybdd.junit5;

import net.serenitybdd.junit5.datadriven.samples.SimpleDataDrivenTestScenarioWithMethodSource;
import net.serenitybdd.junit5.datadriven.samples.SimpleDataDrivenTestScenarioWithMethodSourceExternal;
import net.serenitybdd.junit5.datadriven.samples.SimpleDataDrivenTestScenarioWithMethodSourceNoName;
import net.serenitybdd.junit5.datadriven.samples.SimpleDataDrivenTestScenarioWithValueSource;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnit5DataDrivenAnnotationsTest {


    @Test
    public void testFindValueSourceAnnotatedMethods() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithValueSource.class);
        assertEquals(2,dda.findTestDataMethods().size());
    }

    @Test
    public void testCreateColumnNamesFromValueSource() throws Exception{
        JUnit5DataDrivenAnnotations dda =  new JUnit5DataDrivenAnnotations(SimpleDataDrivenTestScenarioWithValueSource.class);
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
}
