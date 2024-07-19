package net.thucydides.model.output;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.util.ExtendedTemporaryFolder;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static net.thucydides.model.matchers.BeanMatchers.checkThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRecordingTestOutputInASpreadsheet {

    @Rule
    public ExtendedTemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();
    
    
    @Test
    public void should_record_a_test_result_in_a_designated_excel_spreadsheet() throws Exception {

        File outputDir = temporaryFolder.newFolder();
        File outputFile = new File(outputDir, "testresults.xls");
        ResultsOutput output = new SpreadsheetResultsOutput(outputFile, NewList.of("A","B","C"));

        String expectedValue = "$10";
        String actualValue1 = "$10";
        String actualValue2 = "$11";

        output.recordResult(NewList.of("a","b","c"), checkThat(expectedValue, is(actualValue1)));

        output.recordResult(NewList.of("d","e","f"),
                checkThat(expectedValue, is(actualValue2)));

        assertThat(outputFile.exists(), is(true));
    }

    @Test
    public void should_record_test_results_without_checks() throws Exception {

        File outputDir = temporaryFolder.newFolder();
        File outputFile = new File(outputDir, "testresults.xls");
        ResultsOutput output = new SpreadsheetResultsOutput(outputFile, NewList.of("A","B","C"));

        output.recordResult(NewList.of("a","b","c"));
        output.recordResult(NewList.of("d","e","f"));

        assertThat(outputFile.exists(), is(true));
    }

    @Test
    public void should_reset_excel_spreadsheet_if_it_already_exists() throws Exception {

        File outputDir = temporaryFolder.newFolder();
        File outputFile = new File(outputDir, "testresults2.xls");
        ResultsOutput output = new SpreadsheetResultsOutput(outputFile, NewList.of("A","B","C"));

        String expectedValue = "$10";
        String actualValue1 = "$10";
        String actualValue2 = "$11";

        output.recordResult(NewList.of("a","b","c"), checkThat(expectedValue, is(actualValue1)));
        output.recordResult(NewList.of("d","e","f"), checkThat(expectedValue, is(actualValue2)));

        ResultsOutput newOutput = new SpreadsheetResultsOutput(outputFile, NewList.of("A","B","C"));
        newOutput.recordResult(NewList.of("a","b","c"), checkThat(expectedValue, is(actualValue1)));
        newOutput.recordResult(NewList.of("d","e","f"), checkThat(expectedValue, is(actualValue2)));

        assertThat(outputFile.exists(), is(true));
    }
}
