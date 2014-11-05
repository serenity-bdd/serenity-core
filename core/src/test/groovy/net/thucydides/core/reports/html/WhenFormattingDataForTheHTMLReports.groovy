package net.thucydides.core.reports.html

import com.google.common.collect.ImmutableList
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

class WhenFormattingDataForTheHTMLReports extends Specification {

    IssueTracking issueTracking = Mock();

    @Unroll
    def "should display foreign characters as HTML entities"() {
        expect:
        def formatter = new Formatter(issueTracking);
        formatter.htmlCompatible(foreignWord) == formattedWord
        where:
        foreignWord         | formattedWord
        "François"          | "Fran&ccedil;ois"
        "störunterdrückung" | "st&ouml;runterdr&uuml;ckung"
        "CatÃ¡logo"         | "Cat&Atilde;&iexcl;logo"
    }

    @Unroll
    def "should display objects in string form"() {
        expect:
        def formatter = new net.thucydides.core.reports.html.Formatter(issueTracking);
        formatter.htmlCompatible(object) == formattedObject
        where:
        object                          | formattedObject
        [1,2,3]                         | "[1, 2, 3]"
        ["a":"1","b":2]                 | "{a=1, b=2}"
        ImmutableList.of("a","b","c")   | "[a, b, c]"
    }


    @Unroll
    def "should shorten long lines if requested"() {
        expect:
        def formatter = new net.thucydides.core.reports.html.Formatter(issueTracking);
        formatter.truncatedHtmlCompatible(value, length) == formattedValue
        where:
        value                 | length |  formattedValue
        "the quick brown dog" | 3      | "the&hellip;"
        "the quick brown dog" | 10     | "the quick&hellip;"
        "the quick brown dog" | 20     | "the quick brown dog"
        "François"            | 5      | "Fran&ccedil;&hellip;"
    }

    def "should format embedded tables"() {
        given:
        def stepDescription = """Given the following accounts:
   [| owner | points | statusPoints |
    | Jill  | 100000 | 800           |
    | Joe   | 50000  | 50            |]"""
        and:
        def formatter = new Formatter(issueTracking);
        when:
        def formattedDescription = formatter.formatWithFields(stepDescription, [])
        then:
        formattedDescription.contains("Given the following accounts:")
        formattedDescription.contains("<table class='embedded'><thead><th>owner</th><th>points</th><th>statusPoints</th></thead>")
        formattedDescription.contains("<tbody><tr><td>Jill</td><td>100000</td><td>800</td></tr><tr><td>Joe</td><td>50000</td><td>50</td></tr></tbody></table>")
    }

    def "should format multiple embedded tables"() {
        given:
        def stepDescription = """Given the following accounts:
   [| owner | points | statusPoints |
    | Jill  | 100000 | 800           |
    | Joe   | 50000  | 50            |]
    And the following accounts:
   [| owner | points | statusPoints |
    | Jake  | 100000 | 800           |
    | Jane   | 50000  | 50            |]
    """
        and:
        def formatter = new Formatter(issueTracking);
        when:
        def formattedDescription = formatter.formatWithFields(stepDescription, [])
        then:
        formattedDescription.contains("Given the following accounts:")
        formattedDescription.contains("<table class='embedded'><thead><th>owner</th><th>points</th><th>statusPoints</th></thead>")
        formattedDescription.contains("<tbody><tr><td>Jill</td><td>100000</td><td>800</td></tr><tr><td>Joe</td><td>50000</td><td>50</td></tr></tbody></table>")
        formattedDescription.contains("And the following accounts:")
        formattedDescription.contains("<table class='embedded'><thead><th>owner</th><th>points</th><th>statusPoints</th></thead>")
        formattedDescription.contains("<tbody><tr><td>Jake</td><td>100000</td><td>800</td></tr><tr><td>Jane</td><td>50000</td><td>50</td></tr></tbody></table>")
    }

    def "should format raw embedded tables"() {
        given:
        def stepDescription = """Given the following accounts:
    | owner | points | statusPoints |
    | Jill  | 100000 | 800          |
    | Joe   | 50000  | 50           |"""
        and:
        def formatter = new Formatter(issueTracking);
        when:
        def formattedDescription = formatter.formatWithFields(stepDescription, [])
        then:
        formattedDescription.contains("Given the following accounts:")
        formattedDescription.contains("<table class='embedded'><thead><th>owner</th><th>points</th><th>statusPoints</th></thead>")
        formattedDescription.contains("<tbody><tr><td>Jill</td><td>100000</td><td>800</td></tr><tr><td>Joe</td><td>50000</td><td>50</td></tr></tbody></table>")
    }

    def "should format embedded tables without starting and ending pipes"() {
        given:
        def stepDescription = """Given the following accounts:
    owner | points | statusPoints
    Jill  | 100000 | 800
    Joe   | 50000  | 50           """
        and:
        def formatter = new Formatter(issueTracking);
        when:
        def formattedDescription = formatter.formatWithFields(stepDescription, [])
        then:
        formattedDescription.contains("Given the following accounts:")
        formattedDescription.contains("<table class='embedded'><thead><th>owner</th><th>points</th><th>statusPoints</th></thead>")
        formattedDescription.contains("<tbody><tr><td>Jill</td><td>100000</td><td>800</td></tr><tr><td>Joe</td><td>50000</td><td>50</td></tr></tbody></table>")
    }

    def "should format single cell table"() {
        given:
        def singleCellTable = "[|heading|]"
        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "<table class='embedded'><thead><th>heading</th></thead><tbody></tbody></table>"
    }

    def "should ignore table formatting if configured"() {
        given:
        def singleCellTable = "[|heading|]"
        def environmentVariables = new MockEnvironmentVariables()
        def formatter = new Formatter(issueTracking,environmentVariables);
        when:
        environmentVariables.setProperty("ignore.embedded.tables","true")
        and:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == singleCellTable
    }

    def "should cope with pipe that is not in a table"() {
        given:
        def formatter = new Formatter(issueTracking);
        when:
        def noEmbeddedTable = formatter.convertAnyTables("fdg|dsf")
        then:
        noEmbeddedTable == "fdg|dsf"
    }

    def "should format multi cell table"() {
        given:
        def singleCellTable = "[|heading1  |heading2  |]"
        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "<table class='embedded'><thead><th>heading1</th><th>heading2</th></thead><tbody></tbody></table>"
    }

    def "should format a table with a single row"() {
        given:
        def singleCellTable = """[| owner | points |
                                      | Joe   | 50000  |]"""

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "<table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }

    def "should format a table with several rows"() {
        given:
        def singleCellTable = """[| owner | points |
                                  | Jane  | 80000  |
                                  | Joe   | 50000  |]"""

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "<table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }

    def "should identify a table within a step using new lines"() {
        given:
        def singleCellTable = "A table like this:\n[| owner | points |\n| Jane  | 80000  |\n| Joe   | 50000  |]"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "A table like this:<br><table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }

    def "should identify a table within a step using NL new lines"() {
        given:
        def singleCellTable = "A table like this:␤[| owner | points |␤| Jane  | 80000  |␤| Joe   | 50000  |]"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "A table like this:<br><table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }

    def "should identify a table within a step using CTRL-R new lines"() {
        given:
        def singleCellTable = "A table like this:\r[| owner | points |\r| Jane  | 80000  |\r| Joe   | 50000  |]"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "A table like this:<br><table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }


    def "should identify a table within a step using composite Windows new line chars"() {
        given:
        def table = "I have the following document:\r␤|CSV|\r␤|HEADERS|\r␤|values|"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(table)
        then:
        embeddedTable == "I have the following document:<br><table class='embedded'><thead><th>CSV</th></thead><tbody><tr><td>HEADERS</td></tr><tr><td>values</td></tr></tbody></table>"
    }

    def "should identify a table within a step using double spaced lines"() {
        given:
        def table = "I have the following document:\n|CSV|\n\n|HEADERS|\n|values|"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(table)
        then:
        embeddedTable == "I have the following document:<br><table class='embedded'><thead><th>CSV</th></thead><tbody><tr><td>HEADERS</td></tr><tr><td>values</td></tr></tbody></table>"
    }

    def "should identify a table within a step using Windows new lines"() {
        given:
        def singleCellTable = "A table like this:\r\n[| owner | points |\r\n| Jane  | 80000  |\r\n| Joe   | 50000  |]"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "A table like this:<br><table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }


    def "should identify a table within a step using Windows bracket chars"() {
        given:
        def singleCellTable = "A table like this:\r\n［| owner | points |\r\n| Jane  | 80000  |\r\n| Joe   | 50000  |］"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(singleCellTable)
        then:
        embeddedTable == "A table like this:<br><table class='embedded'><thead><th>owner</th><th>points</th></thead><tbody><tr><td>Jane</td><td>80000</td></tr><tr><td>Joe</td><td>50000</td></tr></tbody></table>"
    }

    def "should work with JBehave commands"() {
        given:
        def tableText = "Given a Transmission with the following details: ␤{trim=false} ␤|record| ␤|ABC-123 SOMEBANK, SYDNEY 2000 ADDATE PAGE 1 | ␤|0 GST 8.56- | ␤|lots of filler text | ␤|0 SUB-TOTAL BILLER VALUE 1,962 486,941.32 934.32- 486,007.00| ␤|0 GST 93.56-| ␤|more filler text |"

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(tableText)
        then:
        embeddedTable.contains("<table class='embedded'>")

    }


    def "should work with more JBehave commands"() {

        given:
        def tableText = """Given a Transmission FILE.TYPE.P999999.T123456 with the following details:
    {trim=false}
    |record|
    |\$\$ TRANSACTION STATUS HISTORY 01                                                                                                           |
    |999990065001306259999                                                                                                                      |
    |9999930625092002002814462000000000100019999SUNNY5000 XXX   032916000000000000000099999XXXXXXX Exchange Settlement        NN                |"""

        def formatter = new Formatter(issueTracking);
        when:
        def embeddedTable = formatter.convertAnyTables(tableText)
        then:
        embeddedTable.contains("<table class='embedded'>")
    }


}
