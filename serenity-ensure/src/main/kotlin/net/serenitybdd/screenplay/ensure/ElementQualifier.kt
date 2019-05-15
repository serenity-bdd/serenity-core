package net.serenitybdd.screenplay.ensure

class ElementQualifier(val singularForm: String, val pluralForm: String) {

    companion object {
        val IS_ARE = ElementQualifier("is", "are")
        val HAS_HAVE = ElementQualifier("has", "have")
        val MATCHES_MATCH = ElementQualifier("matches", "match")
    }

    fun resolve(number : GrammaticalNumber) = when(number) {
        GrammaticalNumber.SINGULAR -> singularForm;
        GrammaticalNumber.PLURAL -> pluralForm
    }
}