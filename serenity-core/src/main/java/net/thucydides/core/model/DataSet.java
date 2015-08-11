package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by john on 13/08/2014.
 */
public class DataSet {

    private final int startRow;
    private final String name;
    private final String description;
    private final List<DataTableRow> rows;

    public DataSet(int startRow, int rowCount, String name, String description, List<DataTableRow> rows) {
        this.startRow = startRow;
        this.name = name;
        this.description = description;
        this.rows = extractRows(rows, startRow, rowCount);
    }

    private List<DataTableRow> extractRows(List<DataTableRow> rows, int startRow, int rowCount) {
        int endRow = (rowCount == 0) ? rows.size() : startRow + rowCount;
        return ImmutableList.copyOf(rows.subList(startRow, endRow));
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getStartRow() {
        return startRow;
    }

    public List<DataTableRow> getRows() {
        return rows;
    }
}
