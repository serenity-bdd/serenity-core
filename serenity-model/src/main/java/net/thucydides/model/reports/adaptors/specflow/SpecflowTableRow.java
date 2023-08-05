package net.thucydides.model.reports.adaptors.specflow;

import java.util.List;

public class SpecflowTableRow {

    private final String rowTitle;
    private final List<String> rowSteps;

    public SpecflowTableRow(String rowTitle, List<String> rowSteps) {
        this.rowTitle = rowTitle;
        this.rowSteps = rowSteps;
    }

    public String getRowTitle() {
        return rowTitle;
    }

    public List<String> getRowSteps() {
        return rowSteps;
    }
}
