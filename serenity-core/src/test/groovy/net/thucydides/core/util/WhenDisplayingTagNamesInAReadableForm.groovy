package net.thucydides.core.util

import net.thucydides.model.util.Acronym
import net.thucydides.model.util.Inflector
import spock.lang.Specification

class WhenDisplayingTagNamesInAReadableForm extends Specification {

    def inflection = Inflector.instance

    def "tag names expressed as singular nouns can be displayed in plural form"() {
        when: "I find the plural form of a single word"
        def pluralForm = inflection.of(singleForm).inPluralForm().toString();
        then: "the plural form should be grammatically correct"
        pluralForm == expectedPluralForm
        where:
        singleForm     | expectedPluralForm
        'epic'         | 'epics'
        'feature'      | 'features'
        'story'        | 'stories'
        'stories'      | 'stories'
        'octopus'      | 'octopi'
        'sheep'        | 'sheep'
        'fish'         | 'fish'
        'dynamo'       | 'dynamos'
        'hippopotamus' | 'hippopotamus'
        'parenthesis'  | 'parentheses'
        ''             | ''
    }


    def "should transform plural nouns into singles"() {
        when: "I find the singular form of a single word"
        def singleForm = inflection.of(pluralForm).inSingularForm().toString()
        then: "the singular form should be gramatically correct"
        singleForm == expectedSingleForm
        where:
        pluralForm | expectedSingleForm
        'epics'    | 'epic'
        'features' | 'feature'
        'stories'  | 'story'
        'story'    | 'story'
        'octopi'   | 'octopus'
        'sheep'    | 'sheep'
    }

    def "should transform a number of object nouns into plural or singular based on the number"() {
        when: "we find the plural or singular form of a word"
        def pluralForm = inflection.of(getTotal).times(singleForm).inPluralForm().toString()
        then: "the form should depend on the number of objects"
        pluralForm == expectedPluralForm
        where:
        getTotal | singleForm | expectedPluralForm
        0        | 'epic'     | 'epics'
        1        | 'epic'     | 'epic'
        2        | 'epic'     | 'epics'
    }

    def "should transform camel-case to underscore"() {
        when:
        def underscoreForm = inflection.of(word).withUnderscores().toString()
        then:
        underscoreForm == expectedUnderscoreForm
        where:
        word          | expectedUnderscoreForm
        'aWord'       | 'a_word'
        'AnotherWord' | 'another_word'
    }

    def "should transform kebab-case to underscore"() {
        when:
        def kebabCaseForm = inflection.of(word).withKebabCase().toString()
        then:
        kebabCaseForm == expectedKebabCaseForm
        where:
        word          | expectedKebabCaseForm
        'aWord'       | 'a-word'
        'AnotherWord' | 'another-word'
    }

    def "should captialize first word"() {
        when:
        def capitalized = inflection.of(word).startingWithACapital().toString()
        then:
        capitalized == expectedCapitalizedForm
        where:
        word         | expectedCapitalizedForm
        'epic'       | 'Epic'
        'features'   | 'Features'
        'some story' | 'Some story'
    }

    def "should captialize all words"() {
        when:
        def capitalized = inflection.of(word).asATitle().toString()
        then:
        capitalized == expectedCapitalizedForm
        where:
        word                    | expectedCapitalizedForm
        'epic'                  | 'Epic'
        'x-men: the last stand' | 'X-Men: The Last Stand'
        'X-MEN: the last stand' | 'X-MEN: The Last Stand'
    }

    def "should capitalise all the words in a hyphenated word group (or similar) unless the first word has only one letter"() {
        when:
        def capitalized = inflection.of(word).asATitle().toString()
        then:
        capitalized == expectedCapitalizedForm
        where:
        word                                        | expectedCapitalizedForm
        "two-sided arrow"                           | "Two-Sided Arrow"
        "T-shirt sales slowed in the first quarter" | "T-Shirt Sales Slowed In The First Quarter"
        "E-commerce tricks and tips"                | "E-Commerce Tricks And Tips"
        "E-commerce tricks and tips in JIRA"        | "E-Commerce Tricks And Tips In JIRA"
        "upper-right or lower-right corner"         | "Upper-Right Or Lower-Right Corner"
        "country/region"                            | "Country/Region"
        "turn on the on/off toggle"                 | "Turn On The On/Off Toggle"
        "copy-and-paste support in windows apps"    | "Copy-And-Paste Support In Windows Apps"
        "First test:do something different"         | "First Test:Do Something Different"
        "First test:do something different"         | "First Test:Do Something Different"
        "red(and blue)"                             | "Red(And Blue)"
    }

    def "should not capitalise words after an apostrophe"() {
        when:
        def capitalized = inflection.of(word).asATitle().toString()
        then:
        capitalized == expectedCapitalizedForm
        where:
        word                       | expectedCapitalizedForm
        "including 'quoted' terms" | "Including 'Quoted' Terms"
        "Wolverine's last stand"   | "Wolverine's Last Stand"
    }

    def "should find acronyms in a text"() {
        when:
        def acronyms = Acronym.acronymsIn(word)
        then:
        acronyms.size() == 1
        acronyms == [new Acronym(acronym, start, end)] as Set
        where:
        word             | acronym   | start | end
        'ASCII code'     | 'ASCII'   | 0     | 5
        'big ASCII code' | 'ASCII'   | 4     | 9
        'big AsciI'      | 'AsciI'   | 4     | 9
        'big X1'         | 'X1'      | 4     | 6
        'big FORM16'     | 'FORM16'  | 4     | 10
        'big 1FORM16'    | '1FORM16' | 4     | 11
    }

    def "should find multiple acronyms in a text"() {
        when:
        def acronyms = Acronym.acronymsIn(word)
        then:
        acronyms.size() == count
        where:
        word               | acronym | count
        'ASCII code ASCII' | 'ASCII' | 2
        'big ASCII code'   | 'ASCII' | 1
        'big AsciI CODE'   | 'AsciI' | 2
    }

    def "should restore acronyms in text"() {
        given:
        def acronyms = Acronym.acronymsIn(original)
        when:
        String restoredForm = acronyms[0].restoreIn(lowercase)
        then:
        restoredForm == restored
        where:
        original       | lowercase      | restored
        "The BIG boat" | "The Big Boat" | "The BIG Boat"
        "The big BOAT" | "The Big Boat" | "The Big BOAT"
        "THE big boat" | "The Big Boat" | "THE Big Boat"
        "TOP"          | "top"          | "TOP"

    }


    def "should respect acronyms"() {
        when:
        def capitalized = inflection.of(word).asATitle().toString()
        then:
        capitalized == expectedCapitalizedForm
        where:
        word                   | expectedCapitalizedForm
        'ASCII code'           | 'ASCII Code'
        'the ABC'              | 'The ABC'
        'the QoS RAM'          | 'The QoS RAM'
        'the QoS RAM of QoS'   | 'The QoS RAM Of QoS'
        '__the QoS RAM of QoS' | 'The QoS RAM Of QoS'
    }

    def "should convert variable expressions into human-readable form"() {
        when:
        def humanized = inflection.of(word).inHumanReadableForm().toString()
        then:
        humanized == expectedHumanizedForm
        where:
        word              | expectedHumanizedForm
        'employee_salary' | 'Employee salary'
        'author_id'       | 'Author'
        'someTest'        | 'Some test'
        'AnotherTest'     | 'Another test'
        'AN_ENUM_NAME'    | 'An enum name'
        'ENUM_NAME'       | 'Enum name'
    }
}
