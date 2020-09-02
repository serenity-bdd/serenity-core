package net.thucydides.core.reports.adaptors.specflow;

import net.serenitybdd.core.collect.NewList;

import java.util.ArrayList;
import java.util.List;

public class SpecflowScenario {

    private final String titleLine;
    private List<String> steps = new ArrayList<>();
    private List<SpecflowTableRow> rows;

    public SpecflowScenario(String titleLine) {
        this.titleLine = titleLine;
    }

    public String getTitleLine() {
        return titleLine;
    }

    public void addSteps(List<String> blockSteps) {
        steps.addAll(NewList.copyOf(blockSteps));
    }

    public List<String> getSteps() {
        return NewList.copyOf(steps);
    }

    public void convertToTable(String firstRowTitle) {
        if (rows == null) {
            rows = new ArrayList<>();
            addRow(firstRowTitle, steps);
            steps.clear();
        }

    }

    public boolean usesDataTable() {
        return rows != null;
    }

    public void addRow(String rowTitle, List<String> rowSteps) {
        rows.add(new SpecflowTableRow(rowTitle, NewList.copyOf(rowSteps)));
    }

    public List<SpecflowTableRow> getRows() {
        return NewList.copyOf(rows);
    }
}
