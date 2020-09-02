package net.thucydides.core.csv;

import au.com.bytecode.opencsv.CSVReader;
import net.thucydides.core.steps.stepdata.CSVTestDataSource;
import net.thucydides.core.steps.stepdata.TestDataSource;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenLoadingTestDataFromACSVFile {

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    File temporaryDirectory;

    @Before
    public void setupTemporaryDirectory() throws IOException {
        temporaryDirectory = temporaryFolder.newFolder("testdata");
    }

    protected File useTestDataIn(String filename, String... data) throws IOException {
        File testDataFile = new File(temporaryDirectory, filename);
        testDataFile.setExecutable(true);
        testDataFile.setReadable(true);
        testDataFile.setWritable(true);

        BufferedWriter out = new BufferedWriter(new FileWriter(testDataFile));

        for (String row : data) {
            out.write(row);
            out.newLine();
        }
        out.close();

        return testDataFile;
    }

    @Test
    public void should_be_able_to_load_test_data_from_a_specified_CSV_file() throws IOException {

        // Given
        File testDataFile = useTestDataIn("testdata.csv",
                                          "name, address,        phone",
                                          "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        // When
        List<Map<String,String>> loadedData = testdata.getData();

        // Then
        assertThat(loadedData, is(notNullValue()));
        assertThat(loadedData.size(), is(1));
    }

    @Test
    public void should_be_able_to_load_test_data_from_the_classpath() throws IOException {

        TestDataSource testdata = new CSVTestDataSource("testdata/test.csv");

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData, is(notNullValue()));
        assertThat(loadedData.size(), is(3));
    }


    @Test
    public void should_use_column_headings_to_identify_fields_in_the_test_data() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_unknown_headings() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unused",
                "Bill, 10 main street, 123456789,   extra data here");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_allow_non_comma_separators_to_be_used() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name; address;        phone",
                "Bill; 10 main street, BillVille; 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath(),';');

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street, BillVille"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_unknown_headings_with_no_matching_columns() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unused",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_ignore_extra_columns_in_data_rows() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789,   extra data here");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street"));
        assertThat(row.get("phone"), is("123456789"));
    }

    @Test
    public void should_read_multple_rows_of_data() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789",
                "Tim,  12 main street, 123456700");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();

        assertThat(loadedData.size(), is(2));
        Map<String,String> row1 = loadedData.get(0);
        assertThat(row1.get("name"), is("Bill"));
        assertThat(row1.get("address"), is("10 main street"));
        assertThat(row1.get("phone"), is("123456789"));

        Map<String,String> row2 = loadedData.get(1);
        assertThat(row2.get("name"), is("Tim"));
        assertThat(row2.get("address"), is("12 main street"));
        assertThat(row2.get("phone"), is("123456700"));

    }

    @Test
    public void should_load_nothing_if_no_data_is_present() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv", "");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData.size(), is(0));
    }

    @Test
    public void should_load_nothing_if_only_the_titles_are_present() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                                          "name, address,        phone");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Map<String,String>> loadedData = testdata.getData();
        assertThat(loadedData.size(), is(0));
    }

    @Test
    public void should_load_data_as_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        assertThat(loadedData.size(), is(1));
    }

    @Test
    public void should_load_multiple_rows_of_data_as_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789",
                "Tim,  12 main street, 123456700");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        assertThat(loadedData.size(), is(2));
    }

    @Test
    public void should_assign_fields_in_loaded_objects() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person, allOf(hasProperty("name", is("Bill")),
                hasProperty("address", is("10 main street")),
                hasProperty("phone", is("123456789"))));
    }

    @Test
    public void empty_fields_should_be_set_to_empty_strings() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone",
                "Bill, , 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getAddress(), is(""));
    }

    @Test
    public void unknown_fields_should_be_ignored() throws IOException {
        File testDataFile = useTestDataIn("testdata.csv",
                "name, address,        phone, unknown",
                "Bill, 10 main street, 123456789, whatever");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getName(), is("Bill"));
        assertThat(person.getAddress(), is("10 main street"));
        assertThat(person.getPhone(), is("123456789"));
    }

    @Test
    public void should_work_with_upper_case_column_headings() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "NAME, ADDRESS,        PHONE",
                "Bill, 10 main street, 123456789");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getName(), is("Bill"));
        assertThat(person.getAddress(), is("10 main street"));
        assertThat(person.getPhone(), is("123456789"));
    }

    @Test
    public void should_camel_case_upper_cased_column_headings_with_spaces() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "NAME, ADDRESS,        PHONE, DATE OF BIRTH",
                "Bill, 10 main street, 123456789, 10/10/1980");

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath());

        List<Person> loadedData = testdata.getDataAsInstancesOf(Person.class);
        Person person = loadedData.get(0);
        assertThat(person.getDateOfBirth(), is("10/10/1980"));
    }

    @Test
    public void should_able_handle_escape_sequences() throws IOException {

        File testDataFile = useTestDataIn("testdata.csv",
                "name; address;        phone",
                "Bill; 10 main street, Bill\\nVille; 123456789");
        //  csv file will be crated as below. With \n between Bill and Ville
        //  name; address;        phone
        //  Bill; 10 main street, Bill\nVille; 123456789

        TestDataSource testdata = new CSVTestDataSource(testDataFile.getAbsolutePath(),';', CSVReader.DEFAULT_QUOTE_CHARACTER, '"'); // '"' to handle '\n'

        List<Map<String,String>> loadedData = testdata.getData();
        Map<String,String> row = loadedData.get(0);


        assertThat(row.get("name"), is("Bill"));
        assertThat(row.get("address"), is("10 main street, Bill\\nVille"));
        assertThat(row.get("phone"), is("123456789"));
    }
}
