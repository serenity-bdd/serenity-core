package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Created by john on 13/08/2014.
 */
public class DataSetDescriptor {
    private final int startRow;
    private final int rowCount;
    private final String name;
    private final String description;
    private List<TestTag> tags;

    public static final DataSetDescriptor DEFAULT_DESCRIPTOR = new DataSetDescriptor(0, 0);

    public DataSetDescriptor(int startRow, int rowCount, String name, String description, List<TestTag> tags) {
        this.startRow = startRow;
        this.rowCount = rowCount;
        this.name = name;
        this.description = description;
        this.tags = new ArrayList(tags);
    }

    public DataSetDescriptor(int startrow, int rowCount) {
        this(startrow, rowCount, null, null, emptyList());
    }

    public int getStartRow() {
        return startRow;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DataSetDescriptor withNameAndDescription(String name, String description) {
        return new DataSetDescriptor(startRow, rowCount, name, description, tags);
    }

    public DataSetDescriptor withRowCount(int rowCount) {
        return new DataSetDescriptor(startRow, rowCount, name, description, tags);
    }

    public void addTags(List<TestTag> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public List<TestTag> getTags() {
        return (tags == null) ? emptyList() : new ArrayList<>(tags);
    }

    public DataSetDescriptor forRange(int startRow, int rowCount) {
        return new DataSetDescriptor(startRow, rowCount, name, description, tags);
    }

    public int getLastRow() {
        return (rowCount == 0) ? startRow : startRow + rowCount - 1;
    }
}
