package net.thucydides.core.matchers.dates;

import org.joda.time.DateTime;

import java.util.Locale;

class DateMatcherFormatter {

    public static String formatted(DateTime dateTime) {
        return dateTime.toString("d MMM yyyy HH:mm:ss", Locale.ENGLISH);
    }

}
