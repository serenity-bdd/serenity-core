package net.thucydides.core.reports.json

import net.thucydides.core.model.DataTable
import net.thucydides.core.model.DataTableRow
import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.util.MockEnvironmentVariables
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification

class WhenStoringDataTablesAsJSON extends Specification {

    def environmentVars = new MockEnvironmentVariables();

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()

    @Shared
    def converter = new JacksonJSONConverter(environmentVars)

    def "should read and write a simple data row"() {
        given:
            def dataTableRow = new DataTableRow([1,"two",3.0])
        when:
            StringWriter writer = new StringWriter();
            converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, dataTableRow);
            def renderedJson = writer.toString()
        and:
            def reader = new StringReader(renderedJson)
            def row = converter.mapper.readValue(reader, DataTableRow)
        then:
            row.stringValues == ["1","two","3.0"]
    }

    def "should read and write a simple data row table"() {
        given:
        def row1 = new DataTableRow([1,"two",3.0])
        def row2 = new DataTableRow([4,"five",6.0])
        def dataTable = new DataTable(["a","b","c"],[row1, row2])
        when:
        StringWriter writer = new StringWriter();
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, dataTable);
        def renderedJson = writer.toString()
        and:
        def reader = new StringReader(renderedJson)
        def table = converter.mapper.readValue(reader, DataTable)
        then:
        table.headers == ["a","b","c"]
        table.rows[0].stringValues == ["1","two","3.0"]
        table.rows[1].stringValues == ["4","five","6.0"]
    }

}