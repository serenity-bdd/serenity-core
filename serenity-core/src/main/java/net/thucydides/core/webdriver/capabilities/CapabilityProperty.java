package net.thucydides.core.webdriver.capabilities;

public class CapabilityProperty {

    public static Object asObject(String propertyValue) {

        if (isAQuoted(propertyValue)) {
            return propertyValue;
        }

        try {
            return Integer.parseInt(propertyValue);
        } catch(NumberFormatException noBiggyWeWillTrySomethingElse) {}

        if (propertyValue.equalsIgnoreCase("true") || propertyValue.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(propertyValue);
        }

        return propertyValue;
    }


    private static boolean isAQuoted(String propertyValue) {
        return (surroundedBy("\"", propertyValue) || surroundedBy("'", propertyValue));
    }

    private static boolean surroundedBy(String quote, String propertyValue) {
        return propertyValue.startsWith(quote) && propertyValue.endsWith(quote);
    }

}
