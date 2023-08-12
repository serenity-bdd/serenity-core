package net.thucydides.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 13/08/2014.
 */
public class DataSet {

    private final int startRow;
    private final String name;
    private final String description;
    private final List<DataTableRow> rows;
    private final List<TestTag> tags;

    public DataSet(int startRow, int rowCount, String name, String description, List<DataTableRow> rows, List<TestTag> tags) {
        this.startRow = startRow;
        this.name = name;
        this.description = description;
        this.rows = extractRows(rows, startRow, rowCount);
        this.tags = tags;
    }

    private List<DataTableRow> extractRows(List<DataTableRow> rows, int startRow, int rowCount) {
        int endRow = (rowCount == 0) ? rows.size() : startRow + rowCount;
        return new ArrayList(rows.subList(startRow, endRow));
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

    public List<TestTag> getTags() { return tags; }
}
