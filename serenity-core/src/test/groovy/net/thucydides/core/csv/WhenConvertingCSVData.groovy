package net.thucydides.core.csv

import net.thucydides.core.csv.converters.*
import spock.lang.Specification

class WhenConvertingCSVData extends Specification {

    def "should provide a list of default CSV type converters"() {
        expect:
            !TypeConverters.defaultTypeConverters.empty
    }

    def "should find the correct converter instance for a given data type"() {
        expect:
            TypeConverters.getTypeConverterFor(clazz).class.name == converterType
        where:
            clazz        | converterType
            String       | StringTypeConverter.name
            Integer      | IntegerTypeConverter.name
            BigDecimal   | BigDecimalTypeConverter.name
            Boolean      | BooleanTypeConverter.name
    }

    def "should throw exception if an unknown type is used"() {
        when:
            TypeConverters.getTypeConverterFor(HashMap)
        then:
            thrown(IllegalArgumentException)
    }

    def "should convert values to specified types"() {
        given:
            def converter = TypeConverters.getTypeConverterFor(fieldType)
        when:
            def convertedValue = converter.valueOf(value)
        then:
            convertedValue == expectedValue
        where:
            value      | fieldType  | expectedValue
            "1"        | Integer    |  1
            "1"        | BigDecimal |  1.0
            "1.2"      | BigDecimal |  1.2
            "true"     | Boolean    |  true
            "foo"      | String     |  "foo"
    }
}
