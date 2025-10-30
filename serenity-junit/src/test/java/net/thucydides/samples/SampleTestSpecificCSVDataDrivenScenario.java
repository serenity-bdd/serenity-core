package net.thucydides.samples;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.ManagedPages;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Qualifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityParameterizedRunner.class)
public class SampleTestSpecificCSVDataDrivenScenario {

    private String name;
    private String age;
    private String address;

    public SampleTestSpecificCSVDataDrivenScenario() {
    }

    @Qualifier
    public String getQualifier() {
        return name;
    }

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;

    @Steps
    public SampleScenarioSteps steps;

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void data_driven_test() {
    }

    @Test
    public void another_data_driven_test() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
