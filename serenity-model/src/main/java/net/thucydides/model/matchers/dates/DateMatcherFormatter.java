package net.thucydides.model.matchers.dates;

import org.joda.time.DateTime;

import java.util.Locale;

public class DateMatcherFormatter {

    public static String formatted(DateTime dateTime) {
        return dateTime.toString("d MMM yyyy HH:mm:ss", Locale.ENGLISH);
    }

}
