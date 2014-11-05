package net.thucydides.core.reports.adaptors.specflow;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public class SpecflowScenario {

    private final String titleLine;
    private List<String> steps = Lists.newArrayList();
    private List<SpecflowTableRow> rows;

    public SpecflowScenario(String titleLine) {
        this.titleLine = titleLine;
    }

    public String getTitleLine() {
        return titleLine;
    }

    public void addSteps(List<String> blockSteps) {
        steps.addAll(ImmutableList.copyOf(blockSteps));
    }

    public List<String> getSteps() {
        return ImmutableList.copyOf(steps);
    }

    public void convertToTable(String firstRowTitle) {
        if (rows == null) {
            rows = Lists.newArrayList();
            addRow(firstRowTitle, steps);
            steps.clear();
        }

    }

    public boolean usesDataTable() {
        return rows != null;
    }

    public void addRow(String rowTitle, List<String> rowSteps) {
        rows.add(new SpecflowTableRow(rowTitle, ImmutableList.copyOf(rowSteps)));
    }

    public List<SpecflowTableRow> getRows() {
        return ImmutableList.copyOf(rows);
    }
}
