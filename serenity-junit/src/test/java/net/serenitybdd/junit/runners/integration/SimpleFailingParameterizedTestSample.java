package net.serenitybdd.junit.runners.integration;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.junit.annotations.TestData;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

@RunWith(SerenityParameterizedRunner.class)
public class SimpleFailingParameterizedTestSample {

    protected String userRole = "ROLE";

    public SimpleFailingParameterizedTestSample(String userRole) {
        this.userRole = userRole;
    }

    @TestData(columnNames = "User")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{{"STAFF"}, {"EYEDEMAND_ADMIN"}});
    }

    @Test
    public void test1() {
        System.out.println("test 1 for " + userRole);
    }

    @Test
    public void test2() {
        System.out.println("test 2 for " + userRole);
    }

    @Pending
    @Test
    public void testFailing() {
        throw new AssertionError("failing test");
    }

    @Ignore
    @Test
    public void test4() {
        System.out.println("test 4 for " + userRole);
    }


    @Test
    public void test5() {
        System.out.println("test 5 for " + userRole);
    }

    @Test
    public void testRuntimeError() {
        throw new RuntimeException("runtime error");
    }

}
