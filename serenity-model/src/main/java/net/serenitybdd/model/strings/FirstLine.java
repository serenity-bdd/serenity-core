package net.serenitybdd.model.strings;

import java.util.List;

import static java.util.Arrays.asList;

public class FirstLine {
    public static String of(String text) {
        if ((text == null) || (text.isEmpty())) {
            return "";
        }
        List<String> lines = asList(text.split(("\\r?\\n")));
        return lines.get(0) + "  " + System.lineSeparator();

    }
}
