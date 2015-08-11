package net.thucydides.core.util

import spock.lang.Specification

class WhenDisplayingTagNamesInAReadableForm extends Specification {

    def inflection = Inflector.instance

    def "should transform singular nouns into plurals"() {
        when: "I find the plural form of a single word"
            def pluralForm = inflection.of(singleForm).inPluralForm().toString();
        then: "the plural form should be gramatically correct"
            pluralForm == expectedPluralForm
        where:
            singleForm          | expectedPluralForm
            'epic'              | 'epics'
            'feature'           | 'features'
            'story'             | 'stories'
            'stories'           | 'stories'
            'octopus'           | 'octopi'
            'sheep'             | 'sheep'
            'fish'              | 'fish'
            'dynamo'            | 'dynamos'
            'hippopotamus'      | 'hippopotamus'
            'parenthesis'       | 'parentheses'
            ''                  | ''
    }

    def "should transform plural nouns into singles"() {
        when: "I find the singular form of a single word"
            def singleForm = inflection.of(pluralForm).inSingularForm().toString()
        then: "the singular form should be gramatically correct"
            singleForm == expectedSingleForm
        where:
            pluralForm          | expectedSingleForm
                'epics'             | 'epic'
                'features'          | 'feature'
                'stories'           | 'story'
                'story'             | 'story'
                'octopi'            | 'octopus'
                'sheep'             | 'sheep'
    }

    def "should transform a number of object nouns into plural or singular based on the number"() {
        when: "we find the plural or singular form of a word"
            def pluralForm = inflection.of(getTotal).times(singleForm).inPluralForm().toString()
        then: "the form should depend on the number of objects"
            pluralForm == expectedPluralForm
        where:
            getTotal   | singleForm        | expectedPluralForm
            0       | 'epic'            | 'epics'
            1       | 'epic'            | 'epic'
            2       | 'epic'            | 'epics'
    }

    def "should transform camel-case to underscore"() {
        when:
            def underscoreForm = inflection.of(word).withUnderscores().toString()
        then:
            underscoreForm == expectedUnderscoreForm
        where:
                word              | expectedUnderscoreForm
                    'aWord'        | 'a_word'
                    'AnotherWord'  | 'another_word'
    }

    def "should captialize first word"() {
        when:
            def capitalized = inflection.of(word).startingWithACapital().toString()
        then:
            capitalized == expectedCapitalizedForm
        where:
            word          | expectedCapitalizedForm
            'epic'        | 'Epic'
            'features'    | 'Features'
            'some story'  | 'Some story'
    }

    def "should captialize all words"() {
        when:
            def capitalized = inflection.of(word).asATitle().toString()
        then:
            capitalized == expectedCapitalizedForm
        where:
        word                           | expectedCapitalizedForm
            'epic'                     | 'Epic'
            'x-men: the last stand'    | 'X-Men: The Last Stand'
    }

    def "should convert variable expressions into human-readable form"() {
        when:
            def humanized = inflection.of(word).inHumanReadableForm().toString()
        then:
            humanized == expectedHumanizedForm
        where:
        word          | expectedHumanizedForm
            'employee_salary'   | 'Employee salary'
            'author_id'         | 'Author'
            'someTest'          | 'Some test'
            'AnotherTest'       | 'Another test'
            'AN_ENUM_NAME'      | 'An enum name'
    }
}
