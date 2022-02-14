package net.serenitybdd.screenplay.ui;

class CSSAttributeValue {
    public static String withEscapedQuotes(String value) {
        return value.replace("'", "\\\\'");
    }
}
