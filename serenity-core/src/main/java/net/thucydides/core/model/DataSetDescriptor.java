package net.thucydides.core.model;

/**
 * Created by john on 13/08/2014.
 */
public class DataSetDescriptor {
    private final int startRow;
    private final int rowCount;
    private final String name;
    private final String description;

    public static final DataSetDescriptor DEFAULT_DESCRIPTOR = new DataSetDescriptor(0, 0);

    public DataSetDescriptor(int startRow, int rowCount, String name, String description) {
        this.startRow = startRow;
        this.rowCount = rowCount;
        this.name = name;
        this.description = description;
    }

    public DataSetDescriptor(int startrow, int rowCount) {
        this(startrow, rowCount, null, null);
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
        return new DataSetDescriptor(startRow, rowCount, name, description);
    }

    public DataSetDescriptor withRowCount(int rowCount) {
        return new DataSetDescriptor(startRow, rowCount, name, description);
    }

}
