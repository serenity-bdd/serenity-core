package net.thucydides.core.matchers.dates;

import org.joda.time.DateTime;

class DateMatcherFormatter {

    public static String formatted(DateTime dateTime) {
        return dateTime.toString("d MMM yyyy HH:mm:ss");
    }

}
