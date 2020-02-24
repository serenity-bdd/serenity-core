package net.serenitybdd.junit.runners;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.junit.annotations.Qualifier;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.annotations.UseTestDataFrom;
import net.thucydides.junit.runners.PersonTestScenario;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenFindingTestDataInADataDrivenTest {

    final static class DataDrivenTestScenario {

        @TestData
        public static Collection<Object[]> testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3}
            });
        }
    }

    final static class DataDrivenTestScenarioWithParamNames {

        @TestData(columnNames = "param-A,param-B")
        public static Collection<Object[]> testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3}
            });
        }
    }

    @UseTestDataFrom("test-data/simple-data.csv")
    final static class CSVDataDrivenTestScenario {}

    @Test
    public void the_parameterized_data_method_is_annotated_by_the_TestData_annotation() throws Exception {
        TestClass testClass = new TestClass(DataDrivenTestScenario.class);
        FrameworkMethod method = DataDrivenAnnotations.forClass(testClass).getTestDataMethod();

        assertThat(method.getName(), is("testData"));

    }

    @Test
    public void the_parameterized_data_method_returns_the_set_of_test_data() throws Throwable {
        TestClass testClass = new TestClass(DataDrivenTestScenario.class);
        DataTable testDataTable = DataDrivenAnnotations.forClass(testClass).getParametersTableFromTestDataAnnotation();

        assertThat(testDataTable.getRows().size(), is(3));

    }

    @Test
    public void testData_without_parameter_names_defines_default_parameter_names() throws Throwable {
        TestClass testClass = new TestClass(DataDrivenTestScenario.class);
        DataTable testDataTable = DataDrivenAnnotations.forClass(testClass).getParametersTableFromTestDataAnnotation();
        List<String> parameterNames = testDataTable.getHeaders();

        assertThat(parameterNames.size(), is(2));
        int i = 0;
        for (String parameterName : parameterNames) {
            assertThat(parameterName, is("Parameter " + (i+1)) );
            i++;
        }
    }

    @Test
    public void testData_with_parameter_names_uses_defined_parameter_names() throws Throwable {
        TestClass testClass = new TestClass(DataDrivenTestScenarioWithParamNames.class);
        DataTable testDataTable = DataDrivenAnnotations.forClass(testClass).getParametersTableFromTestDataAnnotation();
        List<String> parameterNames = testDataTable.getHeaders();

        assertThat(parameterNames.size(), is(2));
        assertThat(parameterNames.get(0), is("param-A"));
        assertThat(parameterNames.get(1), is("param-B"));
    }

    @Test
    public void should_be_able_to_count_the_number_of_data_entries() throws Throwable {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenario.class);
        int dataEntries = DataDrivenAnnotations.forClass(testClass).countDataEntries();

        assertThat(dataEntries, is(12));
    }

    @Test
    public void should_be_able_to_get_data_Table_from_csv() throws Throwable {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenario.class);
        DataTable testDataTable = DataDrivenAnnotations.forClass(testClass).getParametersTableFromTestDataSource();

        List<String> parameterNames = testDataTable.getHeaders();

        assertThat(parameterNames.size(), is(3));
        assertThat(parameterNames.get(0), is("NAME"));
        assertThat(parameterNames.get(1), is("AGE"));
        assertThat(parameterNames.get(2), is("ADDRESS"));
    }

    @Test
    public void should_be_able_to_count_the_number_of_data_entries_using_a_class_directory() throws Throwable {
        int dataEntries = DataDrivenAnnotations.forClass(CSVDataDrivenTestScenario.class).countDataEntries();

        assertThat(dataEntries, is(12));
    }

    @Test
    public void should_recognize_a_test_case_with_valid_test_data() {
        TestClass testClass = new TestClass(DataDrivenTestScenario.class);
        assertThat(DataDrivenAnnotations.forClass(testClass).hasTestDataDefined(), is(true));
    }

    final static class DataDrivenTestScenarioWithNoData {}

    @Test
    public void should_recognize_a_test_case_without_valid_test_data() {
        TestClass testClass = new TestClass(DataDrivenTestScenarioWithNoData.class);
        assertThat(DataDrivenAnnotations.forClass(testClass).hasTestDataDefined(), is(false));
    }

    @Test
    public void should_recognize_a_test_case_with_a_valid_test_data_source() {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenario.class);
        assertThat(DataDrivenAnnotations.forClass(testClass).hasTestDataSourceDefined(), is(true));
    }

    @Test
    public void should_recognize_a_test_case_without_a_valid_test_data_source() {
        TestClass testClass = new TestClass(DataDrivenTestScenarioWithNoData.class);
        assertThat(DataDrivenAnnotations.forClass(testClass).hasTestDataSourceDefined(), is(false));
    }

    @Test
    public void should_load_test_class_instances_using_a_provided_test_data_source() throws IOException {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenario.class);
        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass).getDataAsInstancesOf(PersonTestScenario.class);

        assertThat(testScenarios.size(), is(12));
        MatcherAssert.assertThat(testScenarios.get(0).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(1).getName(), is("Jack Black"));
    }

    static class DataDrivenTestScenarioWithPrivateTestData {

        @TestData
        static Collection testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3}
            });
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void the_parameterized_data_method_must_be_public() throws Exception {
        TestClass testClass = new TestClass(DataDrivenTestScenarioWithPrivateTestData.class);
        FrameworkMethod method = DataDrivenAnnotations.forClass(testClass).getTestDataMethod();

        assertThat(method.getName(), is("testData"));

    }

    static class DataDrivenTestScenarioWithNonStaticTestData {

        @TestData
        public Collection testData() {
            return Arrays.asList(new Object[][]{
                    {"a", 1},
                    {"b", 2},
                    {"c", 3}
            });
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void the_parameterized_data_method_must_be_static() throws Exception {
        TestClass testClass = new TestClass(DataDrivenTestScenarioWithNonStaticTestData.class);
        FrameworkMethod method = DataDrivenAnnotations.forClass(testClass).getTestDataMethod();

        assertThat(method.getName(), is("testData"));
    }


    public class SimpleDataDrivenScenario {

        private String name;
        private String address;
        private String phone;

    }

    public class AnnotatedDataDrivenScenario {

        private String name;
        private String address;
        private String phone;

        @Qualifier
        public String getQualifier() {
            return name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    @Test
    public void should_use_the_Qualifier_method_as_a_qualifier_if_present() {
        AnnotatedDataDrivenScenario testCase = new AnnotatedDataDrivenScenario();
        testCase.setName("Joe");

        String qualifier = QualifierFinder.forTestCase(testCase).getQualifier();

        assertThat(qualifier, is("Joe"));
    }

    public static class DataDrivenScenarioWithStaticQualifier {

        @Qualifier
        public static String qualifier() {
            return "QUALIFIER";
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void the_qualifier_method_must_not_be_static() {
        DataDrivenScenarioWithStaticQualifier testCase = new DataDrivenScenarioWithStaticQualifier();
        QualifierFinder.forTestCase(testCase).getQualifier();
    }


    public static class DataDrivenScenarioWithNonPublicQualifier {

        @Qualifier
        protected String qualifier() {
            return "QUALIFIER";
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void the_qualifier_method_must_be_public() {
        DataDrivenScenarioWithNonPublicQualifier testCase = new DataDrivenScenarioWithNonPublicQualifier();
        QualifierFinder.forTestCase(testCase).getQualifier();
    }

    public static class DataDrivenScenarioWithWronlyTypedQualifier {

        @Qualifier
        public int qualifier() {
            return 0;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void the_qualifier_method_must_return_a_string() {
        DataDrivenScenarioWithWronlyTypedQualifier testCase = new DataDrivenScenarioWithWronlyTypedQualifier();
        QualifierFinder.forTestCase(testCase).getQualifier();
    }

    @UseTestDataFrom(value="test-data/simple-semicolon-data.csv", separator=';')
    final static class CSVDataDrivenTestScenarioUsingSemiColons {}

    @Test
    public void should_load_test_class_instances_using_semicolons() throws IOException {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioUsingSemiColons.class);
        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass).getDataAsInstancesOf(PersonTestScenario.class);

        assertThat(testScenarios.size(), is(2));
        MatcherAssert.assertThat(testScenarios.get(0).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(0).getAddress(), is("10 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(1).getName(), is("Jack Black"));
        MatcherAssert.assertThat(testScenarios.get(1).getAddress(), is("1 Main Street, Smithville"));
    }

    @UseTestDataFrom(value="test-data/simple-semicolon-data.csv,test-data/simple-semicolon-data.csv", separator=';')
    final static class CSVDataDrivenTestScenarioFromSeveralSourcesUsingSemiColons {}

    @Test
    public void should_load_test_class_instances_from_several_sources_using_semicolons() throws IOException {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioFromSeveralSourcesUsingSemiColons.class);
        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass).getDataAsInstancesOf(PersonTestScenario.class);

        assertThat(testScenarios.size(), is(4));
        MatcherAssert.assertThat(testScenarios.get(0).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(0).getAddress(), is("10 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(1).getName(), is("Jack Black"));
        MatcherAssert.assertThat(testScenarios.get(1).getAddress(), is("1 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(2).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(2).getAddress(), is("10 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(3).getName(), is("Jack Black"));
        MatcherAssert.assertThat(testScenarios.get(3).getAddress(), is("1 Main Street, Smithville"));
    }

    @Test
    public void should_be_able_to_get_data_Table_from_a_semicolon_delimited_csv() throws Throwable {
        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioUsingSemiColons.class);
        DataTable testDataTable = DataDrivenAnnotations.forClass(testClass).getParametersTableFromTestDataSource();

        List<String> parameterNames = testDataTable.getHeaders();

        assertThat(parameterNames.size(), is(3));
        assertThat(parameterNames.get(0), is("NAME"));
        assertThat(parameterNames.get(1), is("AGE"));
        assertThat(parameterNames.get(2), is("ADDRESS"));
    }



    @UseTestDataFrom(value="$DATADIR/simple-semicolon-data.csv", separator=';')
    final static class CSVDataDrivenTestScenarioFromSpecifiedDataDirectory {}

    @Test
    public void should_load_test_data_from_a_specified_directory() throws IOException {

        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("serenity.data.dir","test-data");
        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioFromSpecifiedDataDirectory.class);

        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass)
                                       .usingEnvironmentVariables(environmentVariables)
                                       .getDataAsInstancesOf(PersonTestScenario.class);

        assertThat(testScenarios.size(), is(2));
        MatcherAssert.assertThat(testScenarios.get(0).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(0).getAddress(), is("10 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(1).getName(), is("Jack Black"));
        MatcherAssert.assertThat(testScenarios.get(1).getAddress(), is("1 Main Street, Smithville"));
    }

    @UseTestDataFrom(value="does-not-exist/simple-semicolon-data.csv,test-data/simple-semicolon-data.csv", separator=';')
    final static class CSVDataDrivenTestScenarioFromSeveralPossibleSources{}

    @Test
    public void should_load_test_data_from_several_possible_sources() throws IOException {

        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioFromSeveralPossibleSources.class);

        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass)
                .getDataAsInstancesOf(PersonTestScenario.class);

        assertThat(testScenarios.size(), is(2));
        MatcherAssert.assertThat(testScenarios.get(0).getName(), is("Joe Smith"));
        MatcherAssert.assertThat(testScenarios.get(0).getAddress(), is("10 Main Street, Smithville"));
        MatcherAssert.assertThat(testScenarios.get(1).getName(), is("Jack Black"));
        MatcherAssert.assertThat(testScenarios.get(1).getAddress(), is("1 Main Street, Smithville"));
    }

    @UseTestDataFrom(value="does-not-exist/simple-semicolon-data.csv,still-does-not-exist/simple-semicolon-data.csv", separator=';')
    final static class CSVDataDrivenTestScenarioFromSeveralPossibleSourcesWithNoValidSource{}

    @Test(expected = IllegalArgumentException.class)
    public void should_load_test_data_from_several_possible_sources_with_no_valid_source() throws IOException {

        TestClass testClass = new TestClass(CSVDataDrivenTestScenarioFromSeveralPossibleSourcesWithNoValidSource.class);

        List<PersonTestScenario> testScenarios
                = DataDrivenAnnotations.forClass(testClass)
                .getDataAsInstancesOf(PersonTestScenario.class);
    }

}
