package net.thucydides.core.model;

import net.thucydides.core.requirements.model.cucumber.ExampleRowResultIcon;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * A table of test data
 */
public class DataTable {
    private final List<String> headers;
    private final List<DataTableRow> rows;
    private final boolean predefinedRows;
    private String scenarioOutline;
    private List<DataSetDescriptor> dataSetDescriptors;
    private transient AtomicInteger currentRow = new AtomicInteger(0);
    private transient Map<Integer, Integer> lineNumbersForEachRow = new HashMap<>();
    private final static String INFO_ICON = "<i class=\"fa fa-info-circle\"></i>";

    private final static List<DataTableRow> NO_ROWS = new ArrayList<>();

    protected DataTable(List<String> headers, List<DataTableRow> rows) {
        this(null, headers, new CopyOnWriteArrayList<>(rows), null, null, Collections.singletonList(DataSetDescriptor.DEFAULT_DESCRIPTOR), null);
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, String title, String description) {
        this(null, headers, new CopyOnWriteArrayList<>(rows), title, description, Collections.singletonList(new DataSetDescriptor(0, 0, title, description, Collections.emptyList())), null);
    }

    protected DataTable(String scenarioOutline,
                        List<String> headers,
                        List<DataTableRow> rows,
                        String title,
                        String description,
                        List<DataSetDescriptor> dataSetDescriptors,
                        Map<Integer, Integer> lineNumbersForEachRow) {
        this.scenarioOutline = scenarioOutline;
        this.headers = headers;
        this.rows = new CopyOnWriteArrayList<>(rows);
        this.predefinedRows = !rows.isEmpty();
        this.dataSetDescriptors = dataSetDescriptors;
        if ((title != null) || (description != null)) {
            setLatestNameAndDescription(title, description);
        }
        this.lineNumbersForEachRow = (lineNumbersForEachRow == null) ? new HashMap<>() : lineNumbersForEachRow;
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, boolean predefinedRows,
                        String scenarioOutline, List<DataSetDescriptor> dataSetDescriptors,
                        AtomicInteger currentRow) {
        this(headers, rows, predefinedRows, scenarioOutline, dataSetDescriptors, currentRow, new ArrayList<>());
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, boolean predefinedRows,
                        String scenarioOutline, List<DataSetDescriptor> dataSetDescriptors,
                        AtomicInteger currentRow, Collection<TestTag> tags) {
        this.headers = headers;
        this.rows = rows;
        this.predefinedRows = predefinedRows;
        this.scenarioOutline = scenarioOutline;
        this.dataSetDescriptors = dataSetDescriptors;
        this.currentRow = currentRow;
    }

    public void addTagsToLatestDataSet(List<TestTag> tags) {
        if (!dataSetDescriptors.isEmpty()) {
            dataSetDescriptors.get(dataSetDescriptors.size() - 1).addTags(tags);
        }
    }

    public Collection<TestTag> getTags() {
        return getExampleTags();
    }

    public void setScenarioOutline(String scenarioOutline) {
        this.scenarioOutline = scenarioOutline;
    }

    public static DataTableBuilder withHeaders(List<String> headers) {
        return new DataTableBuilder(headers);
    }

    Optional<String> scenarioOutline() {
        return Optional.ofNullable(scenarioOutline);
    }

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
    }

    public List<DataTableRow> getRows() {
        return new ArrayList<>(rows);
    }

    public RowValueAccessor row(int rowNumber) {
        return new RowValueAccessor(this, rowNumber);
    }

    public RowValueAccessor nextRow() {
        return new RowValueAccessor(this, nextRowNumber());
    }

    boolean atLastRow() {
        return ((rows.isEmpty()) || (currentRow.get() == rows.size() - 1));
    }

    public RowValueAccessor currentRow() {
        return new RowValueAccessor(this, currentRowNumber());
    }

    private int nextRowNumber() {
        return currentRow.incrementAndGet();
    }

    private int currentRowNumber() {
        return currentRow.intValue();
    }

    public void addRow(Map<String, ?> data) {
        addRow(new DataTableRow(new ArrayList<>(data.values()), 0));
    }

    public void updateLineNumbers(Map<Integer, Integer> lineNumbersOfEachRow) {
        lineNumbersForEachRow().putAll(withLineNumbersIncrementedBy(lineNumbersOfEachRow.size() - 1,lineNumbersOfEachRow));
    }

    private Map<Integer, Integer> lineNumbersForEachRow() {
        if (lineNumbersForEachRow == null) {
            lineNumbersForEachRow = new HashMap<>();
        }
        return lineNumbersForEachRow;
    }

    private Map<? extends Integer, ? extends Integer> withLineNumbersIncrementedBy(int startRow, Map<Integer, Integer> lineNumbersOfEachRow) {
        return lineNumbersOfEachRow.entrySet()
                .stream()
                .collect(toMap(entry -> entry.getKey() + startRow,
                               Map.Entry::getValue));
    }

    public void addRow(List<?> data) {
        addRow(new DataTableRow(new ArrayList<>(data), 0));
    }

    public List<DataSetDescriptor> getDataSetDescriptors() {
        return dataSetDescriptors;
    }

    void addRow(DataTableRow dataTableRow) {
        appendRow(dataTableRow);
        currentRow.set(rows.size() - 1);
    }

    public void appendRow(Map<String, ?> data) {
        appendRow(new DataTableRow(new ArrayList<>(data.values()), 0));
    }

    public void appendRow(List<?> data) {
        appendRow(new DataTableRow(new ArrayList<>(data), 0));
    }

    public void appendRow(DataTableRow dataTableRow) {
        rows.add(dataTableRow);
    }

    public void addRows(List<DataTableRow> rows) {
        for (DataTableRow row : rows) {
            DataTableRow newRow = new DataTableRow(new ArrayList<>(row.getValues()), row.getLineNumber());
            newRow.setResult(row.getResult());
            this.rows.add(newRow);
        }
        currentRow.set(rows.size() - 1);
    }

    private void setLatestNameAndDescription(String name, String description) {
        if ((dataSetDescriptors == null) || (dataSetDescriptors.isEmpty())) {
            dataSetDescriptors = Collections.singletonList(new DataSetDescriptor(0, 0, name, description, Collections.emptyList()));
        } else {
            dataSetDescriptors = replaceLatestDescriptor(last(dataSetDescriptors).withNameAndDescription(name, description));
        }
    }

    private List<DataSetDescriptor> replaceLatestDescriptor(DataSetDescriptor updatedLatestDescriptor) {
        List<DataSetDescriptor> previousDescriptors = dataSetDescriptors.subList(0, dataSetDescriptors.size() - 1);

        List<DataSetDescriptor> descriptors = new ArrayList<>();
        descriptors.addAll(previousDescriptors);
        descriptors.add(updatedLatestDescriptor);
        return descriptors;
    }

    public void startNewDataSet(String name, String description) {
        updateLatestRowCount();

        List<DataSetDescriptor> descriptors = new ArrayList<>();
        descriptors.addAll(dataSetDescriptors);
        descriptors.add(new DataSetDescriptor(rows.size(), 0, name, description, Collections.emptyList()));
        dataSetDescriptors = descriptors;
    }

    private void updateLatestRowCount() {
        DataSetDescriptor currentDescriptor = last(dataSetDescriptors);
        int currentRowCount = rows.size() - currentDescriptor.getStartRow();
        dataSetDescriptors = replaceLatestDescriptor(currentDescriptor.withRowCount(currentRowCount));
    }

    private DataSetDescriptor last(List<DataSetDescriptor> dataSetDescriptors) {
        return dataSetDescriptors.get(dataSetDescriptors.size() - 1);
    }

    boolean hasPredefinedRows() {
        return predefinedRows;
    }

    public int getSize() {
        return rows.size();
    }

    public List<DataSet> getDataSets() {
        List<DataSet> dataSets = new ArrayList<>();
        for (DataSetDescriptor descriptor : dataSetDescriptors) {
            dataSets.add(new DataSet(descriptor.getStartRow(),
                    descriptor.getRowCount(),
                    descriptor.getName(),
                    descriptor.getDescription(),
                    rows,
                    descriptor.getTags()));
        }
        return dataSets;
    }

    void updateRowResultsTo(TestResult result) {
        for (DataTableRow row : rows) {
            row.setResult(result);
        }
    }

    public String toMarkdown(String featureName, String scenarioName) {
        StringBuilder renderedTable = new StringBuilder();

        getDataSetDescriptors().forEach(
                dataSetDescriptor -> {
                    renderTableHeader(renderedTable, dataSetDescriptor);
                    renderTableBody(renderedTable, dataSetDescriptor, featureName, scenarioName);
                }
        );

        return renderedTable.toString();
    }

    private void renderTableBody(StringBuilder renderedTable,
                                 DataSetDescriptor dataSetDescriptor,
                                 String featureName,
                                 String scenarioName) {
        addHeaderTo(renderedTable);
        addSeparatorsTo(renderedTable);
        addRowsTo(renderedTable, dataSetDescriptor, featureName, scenarioName);
    }

    private void addRowsTo(StringBuilder renderedTable,
                           DataSetDescriptor dataSetDescriptor,
                           String featureName,
                           String scenarioName) {
        ExampleRowResultIcon exampleRowCounter = new ExampleRowResultIcon(featureName);

        int startRow = dataSetDescriptor.getStartRow();
        int rowCount = dataSetDescriptor.getRowCount();
        int endRow = (rowCount > 0) ? startRow + rowCount - 1 : getRows().size() - 1;
        for (int row = startRow; row <= endRow; row++) {
            int lineNumber = lineNumbersForEachRow().getOrDefault(row, 0);
            renderedTable.append("| ");
            getRows().get(row).getValues().forEach(value -> renderedTable.append(value).append(" |"));
            renderedTable.append(" ")
                    .append(exampleRowCounter.resultToken(lineNumber))
                    .append(" |")
                    .append(System.lineSeparator());
        }
    }

    private void addSeparatorsTo(StringBuilder renderedTable) {
        renderedTable.append("| ");
        for (int column = 0; column < getHeaders().size(); column++) {
            renderedTable.append("------- | ");
        }
        addSeparatorCellTo(renderedTable);
        renderedTable.append(System.lineSeparator());
    }

    private void addHeaderTo(StringBuilder renderedTable) {
        renderedTable.append("| ");
        getHeaders().forEach(
                header -> renderedTable.append(header).append(" |")
        );
        addBlankCellTo(renderedTable);
        renderedTable.append(System.lineSeparator());
    }

    private void addBlankCellTo(StringBuilder renderedTable) {
        renderedTable.append("    | ");
    }

    private void addSeparatorCellTo(StringBuilder renderedTable) {
        renderedTable.append("--- | ");
    }

    private void renderTableHeader(StringBuilder renderedTable, DataSetDescriptor dataSetDescriptor) {
        renderedTable.append("Examples: ").append(dataSetDescriptor.getName()).append(System.lineSeparator());
        if (isNotEmpty(dataSetDescriptor.getDescription())) {
            renderedTable.append(System.lineSeparator())
                    .append("<div class='example-description'>")
                    .append(INFO_ICON)
                    .append(" ")
                    .append(dataSetDescriptor.getDescription())
                    .append("</div>")
                    .append(System.lineSeparator());
        }
        renderedTable.append(System.lineSeparator());
    }

    public List<TestStep> filterStepsWithTagsFrom(List<TestStep> testSteps, Collection<TestTag> tags) {
        List<DataSetDescriptor> exampleTablesContainingTag = descriptorsWithTagsFrom(tags);

        List<TestStep> filteredSteps = new ArrayList<>();

        exampleTablesContainingTag.forEach(
                table -> {
                    filteredSteps.addAll(
                            testSteps.subList(table.getStartRow(), table.getStartRow() + table.getRowCount())
                    );
                }
        );
        return filteredSteps;
    }


    public DataTable containingOnlyRowsWithTagsFrom(Collection<TestTag> filterTags) {

        if ((getExampleTags() == null) || (getExampleTags().isEmpty())) return this;


        List<DataSetDescriptor> exampleTablesContainingTag = descriptorsWithTagsFrom(filterTags);

        List<DataTableRow> filteredRows = new ArrayList<>();
        List<DataSetDescriptor> reindexedDescriptors = new ArrayList<>();

        int row = 0;
        for (DataSetDescriptor tableDescriptor : exampleTablesContainingTag) {
            filteredRows.addAll(rowsReferencedIn(tableDescriptor));
            reindexedDescriptors.add(tableDescriptor.forRange(row, tableDescriptor.getRowCount()));
            row += tableDescriptor.getRowCount();
        }


        return new DataTable(headers,
                filteredRows,
                predefinedRows,
                scenarioOutline,
                reindexedDescriptors,
                new AtomicInteger(filteredRows.size() - 1));
    }


    public int getLineNumberForRow(int row) {
        return lineNumbersForEachRow().getOrDefault(row, 0);
    }

    private Collection<TestTag> getExampleTags() {
        return dataSetDescriptors.stream().flatMap(
                descriptor -> descriptor.getTags().stream()
        ).collect(Collectors.toList());
    }

    private List<DataSetDescriptor> descriptorsWithTagsFrom(Collection<TestTag> tags) {
        return dataSetDescriptors.stream()
                .filter(dataset -> TestTags.of(dataset.getTags()).containsTagMatchingOneOf(tags))
                .collect(Collectors.toList());
    }

    private Collection<DataTableRow> rowsReferencedIn(DataSetDescriptor tableDescriptor) {
        List<DataTableRow> referencedRows = new ArrayList<>();
        for (int row = tableDescriptor.getStartRow(); row <= tableDescriptor.getLastRow(); row++) {
            referencedRows.add(rows.get(row));
        }
        return referencedRows;
    }

    public Optional<Integer> getResultRowWithLineNumber(int lineNumber) {
        for (int row = 0; row < rows.size(); row++) {
            if (rows.get(row).getLineNumber() == lineNumber) {
                return Optional.of(row);
            }
        }
        return Optional.empty();
    }

    public static class DataTableBuilder {
        private String scenarioOutline;
        private List<String> headers;
        private List<DataTableRow> rows;
        private String description;
        private String title;
        private List<DataSetDescriptor> descriptors;
        private Map<Integer, Integer> rowNumbers;

        DataTableBuilder(List<String> headers) {
            this(null, headers, NO_ROWS, null, null, Collections.singletonList(DataSetDescriptor.DEFAULT_DESCRIPTOR));
        }

        DataTableBuilder(String scenarioOutline, List<String> headers, List<DataTableRow> rows, String title,
                         String description, List<DataSetDescriptor> descriptors) {
            this.scenarioOutline = scenarioOutline;
            this.headers = headers;
            this.rows = rows;
            this.description = description;
            this.title = title;
            this.descriptors = descriptors;
        }

        public DataTableBuilder andScenarioOutline(String scenarioOutline) {
            this.scenarioOutline = scenarioOutline;
            return this;
        }

        public DataTableBuilder andCopyRowDataFrom(DataTableRow row) {
            List<DataTableRow> rows = new ArrayList<>();
            rows.add(new DataTableRow(row.getValues(), row.getLineNumber()));
            this.rows = rows;
            return this;
        }

        public DataTableBuilder andTitle(String title) {
            this.title = title;
            return this;
        }

        public DataTableBuilder andDescription(String description) {
            this.description = description;
            return this;
        }

        public DataTable build() {
            return new DataTable(scenarioOutline, headers, rows, title, description, descriptors, rowNumbers);
        }

        public DataTableBuilder andRows(List<List<Object>> rows) {
            List<DataTableRow> dataTableRows = rows.stream()
                    .map(values -> new DataTableRow(values, 0))
                    .collect(Collectors.toList());

            return new DataTableBuilder(scenarioOutline, headers, dataTableRows, title, description, descriptors);
        }

        public DataTableBuilder andRowData(List<DataTableRow> rows) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }


        public DataTableBuilder andDescriptors(List<DataSetDescriptor> descriptors) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andMappedRows(List<? extends Map<String, ?>> mappedRows) {
            return andMappedRows(mappedRows, new HashMap<>());
        }

        public DataTableBuilder andMappedRows(List<? extends Map<String, ?>> mappedRows,
                                              Map<Integer, Integer> lineNumbers) {

            List<List<Object>> rowData = mappedRows.stream().map(this::rowDataFrom).collect(Collectors.toList());

            AtomicInteger rowNumber = new AtomicInteger();
            List<DataTableRow> dataTableRows = rowData.stream()
                    .map(values -> new DataTableRow(values,
                            lineNumberForRow(lineNumbers, rowNumber)))
                    .collect(Collectors.toList());

            this.rowNumbers = new HashMap<>(lineNumbers);
            this.rows = dataTableRows;
            return this;
        }

        private Integer lineNumberForRow(Map<Integer, Integer> lineNumbers, AtomicInteger rowNumber) {
            return lineNumbers.getOrDefault(rowNumber.getAndIncrement(), 0);
        }

        private List<Object> rowDataFrom(Map<String, ?> mappedRow) {
            List<Object> rowData = new ArrayList<>();
            for (String header : headers) {
                rowData.add(mappedRow.get(header));
            }
            return rowData;
        }

    }

    public static class RowValueAccessor {
        private final DataTable dataTable;
        private final int rowNumber;

        RowValueAccessor(DataTable dataTable, int rowNumber) {
            this.dataTable = dataTable;
            this.rowNumber = rowNumber;
        }

        public void hasResult(TestResult result) {
            dataTable.rows.get(rowNumber).updateResult(result);
        }

        public Map<String, String> toStringMap() {
            Map<String, String> rowData = new HashMap();
            int i = 0;
            for (Object value : dataTable.rows.get(rowNumber).getValues()) {
                rowData.put(dataTable.headers.get(i), value.toString());
                i++;
            }

            return rowData;

        }

    }

    public String restoreVariablesIn(String stepDescription) {
        for (int column = 0; column < getHeaders().size(); column++) {
            String correspondingValueInFirstRow = getRows().get(0).getStringValues().get(column);
            if (isNotEmpty(correspondingValueInFirstRow)) {
                stepDescription = stepDescription.replaceAll("\\b" + withEscapedRegExChars(correspondingValueInFirstRow) + "\\b", "{{" + column + "}}");
            }
        }

        int field = 0;
        for (String header : getHeaders()) {
            stepDescription = StringUtils.replace(stepDescription, "{{" + field + "}}", "<" + header + ">");
            field++;
        }

        return stepDescription;
    }

    private static String[] REGEX_CHARS = new String[]{
            "{", "}", "(", ")", "[", "]", "\\", ".", "?", "*", "+", "^", "$", "|"
    };

    private static String[] ESCAPED_REGEX_CHARS = new String[]{
            "\\{", "\\}", "\\(", "\\)", "\\[", "\\]", "\\\\", "\\.", "\\?", "\\*", "\\+", "\\^", "\\$", "\\|"
    };

    private String withEscapedRegExChars(String value) {
        return StringUtils.replaceEach(value, REGEX_CHARS, ESCAPED_REGEX_CHARS);
    }
}
