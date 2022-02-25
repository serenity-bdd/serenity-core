package net.serenitybdd.screenplay.ui;

class XMLAttributeValue {
    public static String withEscapedQuotes(String value) {
        return value.replace("'", "&apos;");
    }
}
