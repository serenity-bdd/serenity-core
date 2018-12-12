package net.serenitybdd.core.pages;

public class ParameterisedLocator {
    public static String withArguments(String locator, Object... arguments) {
        int index = 0;
        for(Object arg : arguments) {
            locator = locator.replace("{" + index++ + "}", arg.toString());
        }
        return locator;
    }
}
