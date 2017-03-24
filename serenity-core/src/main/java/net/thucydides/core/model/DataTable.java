package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.lambdaj.Lambda.convert;

/**
 * A table of test data
 */
public class DataTable {
    private final List<String> headers;
    private final List<DataTableRow> rows;
    private final boolean predefinedRows;
    private final String scenarioOutline;
    private List<DataSetDescriptor> dataSetDescriptors;
    private transient AtomicInteger currentRow = new AtomicInteger(0);

    private final static List<DataTableRow> NO_ROWS = Lists.newArrayList();

    protected DataTable(List<String> headers, List<DataTableRow> rows) {
        this(null, headers, new CopyOnWriteArrayList(rows), null, null, ImmutableList.of(DataSetDescriptor.DEFAULT_DESCRIPTOR));
    }

    protected DataTable(List<String> headers, List<DataTableRow> rows, String title, String description) {
        this(null, headers, new CopyOnWriteArrayList(rows), title, description, ImmutableList.of(new DataSetDescriptor(0,0,title, description)));
    }

    protected DataTable(String scenarioOutline, List<String> headers, List<DataTableRow> rows, String title, String description, List<DataSetDescriptor> dataSetDescriptors) {
        this.scenarioOutline = scenarioOutline;
        this.headers = headers;
        this.rows = new CopyOnWriteArrayList(rows);
        this.predefinedRows = !rows.isEmpty();
        this.dataSetDescriptors = dataSetDescriptors;
        if ((title != null) || (description != null)) {
            setLatestNameAndDescription(title, description);
        }
    }

    public static DataTableBuilder withHeaders(List<String> headers) {
        return new DataTableBuilder(headers);
    }

    public Optional<String> scenarioOutline() {
        return Optional.fromNullable(scenarioOutline);
    }

    public List<String> getHeaders() {
        return ImmutableList.copyOf(headers);
    }

    public List<DataTableRow> getRows() {
        return ImmutableList.copyOf(rows);
    }

    public RowValueAccessor row(int rowNumber) {
        return new RowValueAccessor(this, rowNumber);
    }

    public RowValueAccessor nextRow() {
        return new RowValueAccessor(this, nextRowNumber());
    }

    public boolean atLastRow() {
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

    public void addRow(Map<String, ? extends Object> data) {
        addRow(new DataTableRow(ImmutableList.copyOf(data.values())));
    }

    public List<DataSetDescriptor> getDataSetDescriptors() {
        return dataSetDescriptors;
    }

    public void addRow(DataTableRow dataTableRow) {
        appendRow(dataTableRow);
        currentRow.set(rows.size() - 1);
    }

    public void appendRow(Map<String, ? extends Object> data) {
        appendRow(new DataTableRow(ImmutableList.copyOf(data.values())));
    }

    public void appendRow(DataTableRow dataTableRow) {
        rows.add(dataTableRow);
    }

    public void addRows(List<DataTableRow> rows) {
        for (DataTableRow row : rows) {
            DataTableRow newRow = new DataTableRow(ImmutableList.copyOf(row.getValues()));
            newRow.setResult(row.getResult());
            this.rows.add(newRow);
        }
        currentRow.set(rows.size() - 1);
    }

    public void setLatestNameAndDescription(String name, String description) {
        if ((dataSetDescriptors == null) || (dataSetDescriptors.isEmpty())) {
            dataSetDescriptors = ImmutableList.of(new DataSetDescriptor(0,0,name,description));
        } else {
            dataSetDescriptors = replaceLatestDescriptor(last(dataSetDescriptors).withNameAndDescription(name, description));
        }
    }

    private List<DataSetDescriptor> replaceLatestDescriptor(DataSetDescriptor updatedLatestDescriptor) {
        List<DataSetDescriptor> previousDescriptors = dataSetDescriptors.subList(0, dataSetDescriptors.size() - 1);

        return new ImmutableList.Builder<DataSetDescriptor>()
                .addAll(previousDescriptors)
                .add(updatedLatestDescriptor)
                .build();
    }

    public void startNewDataSet(String name, String description) {
        updateLatestRowCount();
        dataSetDescriptors = new ImmutableList.Builder<DataSetDescriptor>()
                            .addAll(dataSetDescriptors)
                            .add(new DataSetDescriptor(rows.size(), 0, name, description))
                            .build();


    }

    private void updateLatestRowCount() {
        DataSetDescriptor currentDescriptor = last(dataSetDescriptors);
        int currentRowCount = rows.size() - currentDescriptor.getStartRow();
        dataSetDescriptors = replaceLatestDescriptor(currentDescriptor.withRowCount(currentRowCount));
    }

    private DataSetDescriptor last(List<DataSetDescriptor> dataSetDescriptors) {
        return dataSetDescriptors.get(dataSetDescriptors.size() - 1);
    }

    public boolean hasPredefinedRows() {
        return predefinedRows;
    }

    public int getSize() {
        return rows.size();
    }

    public List<DataSet> getDataSets() {
        List<DataSet> dataSets = Lists.newArrayList();
        for (DataSetDescriptor descriptor : dataSetDescriptors) {
            dataSets.add(new DataSet(descriptor.getStartRow(),
                    descriptor.getRowCount(),
                    descriptor.getName(),
                    descriptor.getDescription(),
                    rows));
        }
        return dataSets;
    }

    public void updateRowResultsTo(TestResult result) {
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

        public DataTableBuilder(List<String> headers) {
            this(null, headers, NO_ROWS, null, null, ImmutableList.of(DataSetDescriptor.DEFAULT_DESCRIPTOR));
        }

        public DataTableBuilder(String scenarioOutline, List<String> headers, List<DataTableRow> rows, String title,
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
            List<DataTableRow> rows = new ArrayList<DataTableRow>();
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
            return new DataTableBuilder(scenarioOutline, headers, convert(rows, toDataTableRows()), title, description, descriptors);
        }

        public DataTableBuilder andRowData(List<DataTableRow> rows) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }


        public DataTableBuilder andDescriptors(List<DataSetDescriptor> descriptors) {
            return new DataTableBuilder(scenarioOutline, headers, rows, title, description, descriptors);
        }

        public DataTableBuilder andMappedRows(List<? extends Map<String, ? extends Object>> mappedRows) {
            List<List<Object>> rowData = Lists.newArrayList();
            for (Map<String, ? extends Object> mappedRow : mappedRows) {
                rowData.add(rowDataFrom(mappedRow));
            }
            return new DataTableBuilder(scenarioOutline, headers, convert(rowData, toDataTableRows()), title, description, descriptors);
        }

        private Converter<List<Object>, DataTableRow> toDataTableRows() {
            return new Converter<List<Object>, DataTableRow>() {

                public DataTableRow convert(List<Object> values) {
                    return new DataTableRow(values);
                }
            };
        }

        private List<Object> rowDataFrom(Map<String, ? extends Object> mappedRow) {
            List<Object> rowData = Lists.newArrayList();
            for (String header : headers) {
                rowData.add(mappedRow.get(header));
            }
            return rowData;
        }

    }

    public static class RowValueAccessor {
        private final DataTable dataTable;
        private final int rowNumber;

        public RowValueAccessor(DataTable dataTable, int rowNumber) {
            this.dataTable = dataTable;
            this.rowNumber = rowNumber;
        }

        public void hasResult(TestResult result) {
            dataTable.rows.get(rowNumber).updateResult(result);
        }

        public Map<String, String> toStringMap() {
            Map<String, String> rowData = new HashMap<String, String>();
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
