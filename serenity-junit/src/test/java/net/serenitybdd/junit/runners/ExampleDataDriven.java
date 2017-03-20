package net.serenitybdd.junit.runners;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.Qualifier;
import net.thucydides.junit.annotations.TestData;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
@Concurrent
public class ExampleDataDriven {

    @Steps
    ExampleSteps exampleSteps;

    @TestData
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
            {"first"},
            {"second"},
            {"third"}
        });
    }

    @Qualifier
    public String qualifier() {
        return String.format("%s test", o.toString());
    }

    private final Object o;

    public ExampleDataDriven(Object o) {
        this.o = o;
    }

    @Test
    public void exampleDataDriven() {
        exampleSteps.pageShouldContainElements("1", "Name");
    }

    static public class ExampleSteps {

        @Step("Page should contain [{0}] [{1}] element(s)")
        public void pageShouldContainElements(Object value, Object name) {

        }
    }
}