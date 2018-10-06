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

        String textWithAJBehaveTable = "Given the following transaction is reportable\n" +
                "［| Trade ID | Jurisdiction | State      |\n" +
                "| 100      | MIFID-2      | Reportable |\n" +
                "| 101      | MIFID-2      | Reportable |］";

        assertThat(MarkdownTables.convertTablesIn(textWithAJBehaveTable))
                .isEqualTo("Given the following transaction is reportable\n\n" +
                        "| Trade ID | Jurisdiction | State      |  \n" +
                        "| --- | --- | --- |  \n" +
                        "| 100      | MIFID-2      | Reportable |  \n" +
                        "| 101      | MIFID-2      | Reportable |  \n");
    }

}
