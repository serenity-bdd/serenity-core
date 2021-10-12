package net.serenitybdd.screenplay.ui;

class AttributeValue {
    public static String withEscapedQuotes(String value) {
        return value.replace("'","&#39;");
    }
}
