package net.serenitybdd.junit5.datadriven.samples;


import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.samples.SampleScenarioSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;


@ExtendWith(SerenityJUnit5Extension.class)
public class SampleCSVDataDrivenScenario {

    private String name;
    private String age;
    private String address;

    public SampleCSVDataDrivenScenario() {
    }

    /*@Qualifier
    public String getQualifier() {
        return name;
    }

    @Managed(driver = "chrome", options="--headless")
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "classpath:static-site/index.html")
    public Pages pages;*/

    @Steps
    public SampleScenarioSteps steps;

    //TODO - it must be a {} , otherwise the tests outcomes have the same name
    @ParameterizedTest(name = "Csv File Data Test {0}")
    @CsvFileSource(resources="/test-data/simple-data.csv",numLinesToSkip = 1)
    public void data_driven_test(String name, int age,String address) {
//        assertThat(name).isNotEqualTo("Paul Brown");
    }

    @ParameterizedTest(name = "Another Csv File Data Test {0} ")
    @CsvFileSource(resources="/test-data/simple-data.csv",numLinesToSkip = 1)
    public void another_data_driven_test(String name, int age,String address) {}

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
