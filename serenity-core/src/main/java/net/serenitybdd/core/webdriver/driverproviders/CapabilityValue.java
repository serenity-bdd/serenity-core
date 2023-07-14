package net.serenitybdd.core.webdriver.driverproviders;

public class CapabilityValue {
    public static Object fromString(Object value) {
        if (isaBoolean(value)) {
            return Boolean.parseBoolean(value.toString());
        }
        if (isAnInteger(value)) {
            return Integer.parseInt(value.toString());
        }
        return value;
    }

    private static boolean isaBoolean(Object value) {
        return value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false");
    }

    private static boolean isAnInteger(Object value) {
        try {
            Integer.parseInt(value.toString());
            return true;
        } catch(NumberFormatException notAnInteger) {
            return false;
        }
    }
}
