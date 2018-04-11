package net.thucydides.core.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A table of test data
 */
public class DataTable {
    private final List<String> headers;
    private final List<DataTableRow> rows;
    private final boolean predefinedRows;
    private String scenarioOutline;
    private List<DataSetDescriptor> dataSetDescriptors;
    private List<TestTag> tags;
    private transient AtomicInteger currentRow = new AtomicInteger(0);

    private final static List<DataTableRow> NO_ROWS = new ArrayList<>();

    protected DataTable(List<String> headers, List<DataTableRow> rows) {
        this(null, headers, new CopyOnWriteArrayList<>(rows), null, null, Collections.singletonList(DataSetDescriptor.DEFAULT_DESCRIPTOR));
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, String title, String description) {
        this(null, headers, new CopyOnWriteArrayList<>(rows), title, description, Collections.singletonList(new DataSetDescriptor(0,0,title, description, Collections.emptyList())));
    }

    protected DataTable(String scenarioOutline, List<String> headers, List<DataTableRow> rows, String title, String description, List<DataSetDescriptor> dataSetDescriptors) {
        this.scenarioOutline = scenarioOutline;
        this.headers = headers;
        this.rows = new CopyOnWriteArrayList<>(rows);
        this.predefinedRows = !rows.isEmpty();
        this.dataSetDescriptors = dataSetDescriptors;
        this.tags = new ArrayList<>();
        if ((title != null) || (description != null)) {
            setLatestNameAndDescription(title, description);
        }
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, boolean predefinedRows,
                        String scenarioOutline, List<DataSetDescriptor> dataSetDescriptors,
                        AtomicInteger currentRow) {
        this.headers = headers;
        this.rows = rows;
        this.predefinedRows = predefinedRows;
        this.scenarioOutline = scenarioOutline;
        this.dataSetDescriptors = dataSetDescriptors;
        this.currentRow = currentRow;
        this.tags = new ArrayList<>();
    }

    public void addTagsToLatestDataSet(List<TestTag> tags) {
        if (!dataSetDescriptors.isEmpty()) {
            dataSetDescriptors.get(dataSetDescriptors.size() - 1).addTags(tags);
        }
    }

    public List<TestTag> getTags() {
        return new ArrayList<>(tags);
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
        addRow(new DataTableRow(new ArrayList<>(data.values())));
    }

    public void addRow(List<?> data) {
        addRow(new DataTableRow(new ArrayList<>(data)));
    }

    public List<DataSetDescriptor> getDataSetDescriptors() {
        return dataSetDescriptors;
    }

    void addRow(DataTableRow dataTableRow) {
        appendRow(dataTableRow);
        currentRow.set(rows.size() - 1);
    }

    public void appendRow(Map<String, ?> data) {
        appendRow(new DataTableRow(new ArrayList<>(data.values())));
    }

    public void appendRow(List<?> data) {
        appendRow(new DataTableRow(new ArrayList<>(data)));
    }

    void appendRow(DataTableRow dataTableRow) {
        rows.add(dataTableRow);
    }

    public void addRows(List<DataTableRow> rows) {
        for (DataTableRow row : rows) {
            DataTableRow newRow = new DataTableRow(new ArrayList<>(row.getValues()));
            newRow.setResult(row.getResult());
            this.rows.add(newRow);
        }
        currentRow.set(rows.size() - 1);
    }

    private void setLatestNameAndDescription(String name, String description) {
        if ((dataSetDescriptors == null) || (dataSetDescriptors.isEmpty())) {
            dataSetDescriptors = Collections.singletonList(new DataSetDescriptor(0,0,name,description, Collections.emptyList()));
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
        for(DataTableRow row : rows) {
            row.setResult(result);
        }
    }

    public static class DataTableBuilder {
        final String scenarioOutline;
        final List<String> headers;
        final List<DataTableRow> rows;
        final String description;
        final String title;
        final List<DataSetDescriptor> descriptors;

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
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andCopyRowDataFrom(DataTableRow row) {
            List<DataTableRow> rows = new ArrayList<>();
            rows.add(new DataTableRow(row.getValues()));
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andTitle(String title) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andDescription(String description) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTable build() {
            return new DataTable(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andRows(List<List<Object>> rows) {
            List<DataTableRow> dataTableRows = rows.stream()
                    .map(DataTableRow::new)
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
            List<List<Object>> rowData = new ArrayList<>();
            for (Map<String, ?> mappedRow : mappedRows) {
                rowData.add(rowDataFrom(mappedRow));
            }

            List<DataTableRow> dataTableRows = rowData.stream()
                    .map(DataTableRow::new)
                    .collect(Collectors.toList());

            return new DataTableBuilder(scenarioOutline, headers, dataTableRows, title, description, descriptors);
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
        for(int column = 0; column < getHeaders().size(); column++) {
            String correspondingValueInFirstRow = getRows().get(0).getStringValues().get(column);
            if (StringUtils.isNotEmpty(correspondingValueInFirstRow)) {
                stepDescription = stepDescription.replaceAll("\\b" + withEscapedRegExChars(correspondingValueInFirstRow) + "\\b", "{{" + column + "}}");
            }
        }

        int field = 0;
        for(String header : getHeaders()) {
            stepDescription = StringUtils.replace(stepDescription, "{{" + field + "}}", "<" + header + ">");
            field++;
        }

        return stepDescription;
    }

    private static String[] REGEX_CHARS = new String[] {
            "{","}","(",")","[","]","\\",".","?","*","+","^","$","|"
    };

    private static String[] ESCAPED_REGEX_CHARS = new String[] {
            "\\{","\\}","\\(","\\)","\\[","\\]","\\\\","\\.","\\?","\\*","\\+","\\^","\\$","\\|"
    };

    private String withEscapedRegExChars(String value) {
        return StringUtils.replaceEach(value, REGEX_CHARS, ESCAPED_REGEX_CHARS);
    }
}
