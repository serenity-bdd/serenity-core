package net.thucydides.model.output;

import jxl.JXLException;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.*;
import net.thucydides.model.matchers.SimpleValueMatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpreadsheetResultsOutput implements ResultsOutput {
    List<String> titles;
    File outputFile;
    boolean recordingStarted;

    public SpreadsheetResultsOutput(File outputFile, List<String> titles) {
        this.titles = new ArrayList<>(titles);
        this.outputFile = outputFile;
        recordingStarted = false;

    }

    @Override
    public synchronized void recordResult(List<? extends Object> columnValues,
                                          SimpleValueMatcher... validityChecks) throws IOException {

        WritableWorkbook workbook = null;

        try {
            workbook = currentWorkbook();
            writeRow(columnValues, workbook.getSheet(0), validityChecks);
            workbook.write();
        } catch (JXLException e) {
            throw new IOException(e);
        } finally {
            close(workbook);
        }
    }

    private void close(WritableWorkbook workbook) throws IOException {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (WriteException ignoredException) {}
    }

    private void writeRow(List<? extends Object> columnValues,
                          WritableSheet sheet,
                          SimpleValueMatcher... checks) throws WriteException {

        recordingStarted = true;

        boolean isAFailedTest = checkIfTestHasFailed(checks);
        WritableCellFormat font = getFontFor(isAFailedTest);

        int row = sheet.getRows();
        int column = 0;
        for (Object columnValue : columnValues) {
            Label resultCell = new Label(column++, row, columnValue.toString(), font);
            sheet.addCell(resultCell);
        }
    }

    private boolean checkIfTestHasFailed(SimpleValueMatcher[] checks) {
        boolean isAFailedTest = false;
        for(SimpleValueMatcher check : checks) {
            if (!check.matches()) {
                isAFailedTest = true;
            }
        }
        return isAFailedTest;
    }

    private WritableCellFormat getFontFor(boolean aFailedTest) throws WriteException {
        WritableFont baseFont = new WritableFont(WritableFont.ARIAL, 10);
        if (aFailedTest) {
            baseFont.setBoldStyle(WritableFont.BOLD);
            baseFont.setColour(Colour.RED);
        }
        return new WritableCellFormat(baseFont);
    }

    private WritableWorkbook currentWorkbook() throws IOException, BiffException {
        WritableWorkbook workbook;
        if (!recordingStarted || !outputFile.exists()) {
            workbook = createNewSpreadSheet(outputFile);
        } else {
            workbook = openExistingSpreadsheet();
        }
        return workbook;
    }

    private WritableWorkbook openExistingSpreadsheet() throws BiffException, IOException {
        return Workbook.createWorkbook(outputFile, Workbook.getWorkbook(outputFile));
    }

    private WritableWorkbook createNewSpreadSheet(File outputFile) throws IOException {
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));

            WritableWorkbook workbook = Workbook.createWorkbook(outputFile, wbSettings);
            workbook.createSheet("Test Results", 0);
            WritableSheet sheet = workbook.getSheet(0);

            int cellIndex = 0;
            for (String title : titles) {
                Label label = new Label(cellIndex++, 0, title);
                sheet.addCell(label);
            }
            return workbook;
        } catch (JXLException e) {
            throw new IOException(e);
        }
    }
}
