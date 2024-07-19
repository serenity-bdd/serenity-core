package net.thucydides.model.annotations

import net.serenitybdd.annotations.Fields
import spock.lang.Specification

class WhenReadingFieldsInAClass extends Specification {


    static class SomeClass {
        static String A_STATIC_FIELD = "static"
        String aNormalField
    }

   def "should find all the fields in a class"() {
       when:
            def allFields = Fields.of(SomeClass).allFields()
            def fieldNames = allFields.collect { it.name }
       then:
            "aNormalField" in fieldNames && "A_STATIC_FIELD" in fieldNames
   }

    def "should find all the non-static fields in a class"() {
        when:
            def nonStaticFields = Fields.of(SomeClass).nonStaticFields()
            def fieldNames = nonStaticFields.collect { it.name }
        then:
            "aNormalField" in fieldNames
            !("A_STATIC_FIELD" in fieldNames)
    }

}
