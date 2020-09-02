package net.thucydides.core.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.Date;

public class DateCollectionContainsSameDatesMatcher extends TypeSafeMatcher<Collection<Date>> {
    private final Collection<Date> expectedDates;

    public DateCollectionContainsSameDatesMatcher(final Collection<Date> expectedDates) {
        this.expectedDates = expectedDates;
    }

    @Override
    public boolean matchesSafely(Collection<Date> dates) {
        if (dates.size() != expectedDates.size()) {
            return false;
        }
        for (Date expectedDate : expectedDates) {
            if (!hasIdenticalDate(expectedDate, dates)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasIdenticalDate(Date expectedDate, Collection<Date> dates) {
        for (Date date : dates) {
            if ((DateComparator.sameDate(date, expectedDate))) {
                return true;
            }
        }
        return false; 
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a collection of dates containing ");
        description.appendValueList ("[",",","]", expectedDates);
    }
}