package net.thucydides.core.reports;

import net.thucydides.core.reports.html.MarkdownTables;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenRenderingJBehaveTables {


    @Test
    public void shouldNotDoAnythingIfNoJBehaveTableIsPresent() {
        assertThat(MarkdownTables.convertTablesIn("no tables")).isEqualTo("no tables");
    }

    @Test
    public void shouldNotDoAnythingIfTextIsNull() {
        assertThat(MarkdownTables.convertTablesIn(null)).isNull();
    }

    @Test
    public void shouldRenderJBehaveTables() {

        String textWithAJBehaveTable = "Given the following transaction is reportable" + System.lineSeparator() +
                "［| Trade ID | Jurisdiction | State      |" + System.lineSeparator() +
                "| 100      | MIFID-2      | Reportable |" + System.lineSeparator() +
                "| 101      | MIFID-2      | Reportable |］";

        assertThat(MarkdownTables.convertTablesIn(textWithAJBehaveTable))
                .isEqualTo("Given the following transaction is reportable" + System.lineSeparator() + System.lineSeparator() +
                        "| Trade ID | Jurisdiction | State      |  " + System.lineSeparator() +
                        "| --- | --- | --- |  " + System.lineSeparator() +
                        "| 100      | MIFID-2      | Reportable |  " + System.lineSeparator() +
                        "| 101      | MIFID-2      | Reportable |  " + System.lineSeparator());
    }

}
