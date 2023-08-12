package net.thucydides.core.reports.html

import net.thucydides.model.reports.html.ExampleTable
import spock.lang.Specification

class WhenFormattingEmbeddedTables extends Specification {

    def "should format simple tables"() {
        given:
            def tableText = """
| NAME |
| Joe  |
"""
        when:
            def exampleTable = new ExampleTable(tableText)
        then:
            exampleTable.headers == ["NAME"]
        and:
            exampleTable.rows == [["Joe"]]
    }

    def "should format tables with Windows-specific new line chars"() {
        given:
        def tableText = """
| NAME |␤| Joe  |
"""
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["NAME"]
        and:
        exampleTable.rows == [["Joe"]]
    }

    def "should format tables with composite Windows-specific new line chars"() {
        given:
        def tableText = "| NAME |\r␤| Joe  |"
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["NAME"]
        and:
        exampleTable.rows == [["Joe"]]
    }
    def "should format multi-column tables"() {
        given:
            def tableText = """
| NAME | AGE |
| Joe  | 20  |
"""
        when:
          def exampleTable = new ExampleTable(tableText)
        then:
            exampleTable.headers == ["NAME", "AGE"]
        and:
            exampleTable.rows == [["Joe", "20"]]
    }


    def "should format multi-line tables"() {
        given:
        def tableText = """
| NAME | AGE |
| Joe  | 20  |
| Jill | 21  |
"""
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["NAME", "AGE"]
        and:
        exampleTable.rows == [["Joe", "20"],["Jill","21"]]
    }


    def "should format multi-column tables without leading pipe"() {
        given:
            def tableText = """
 NAME | AGE |
 Joe  | 20  |
"""
        when:
            def exampleTable = new ExampleTable(tableText)
        then:
            exampleTable.headers == ["NAME", "AGE"]
        and:
            exampleTable.rows == [["Joe", "20"]]
    }

    def "should format multi-column tables without tailing pipe"() {
        given:
        def tableText = """
 NAME | AGE
 Joe  | 20
"""
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["NAME", "AGE"]
        and:
        exampleTable.rows == [["Joe", "20"]]
    }

    def "should cope with inconsistant pipes"() {
        given:
        def tableText = """
 NAME | AGE
 Joe  | 20 |
"""
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["NAME", "AGE"]
        and:
        exampleTable.rows == [["Joe", "20"]]
    }

    def "should strip surrounding square brackets for tables"() {
        given:
        def tableText = """
               [| owner | points |
                | Joe   | 50000  |]
"""
        when:
        def exampleTable = new ExampleTable(tableText)
        then:
        exampleTable.headers == ["owner", "points"]
        and:
        exampleTable.rows == [["Joe", "50000"]]
    }
}
