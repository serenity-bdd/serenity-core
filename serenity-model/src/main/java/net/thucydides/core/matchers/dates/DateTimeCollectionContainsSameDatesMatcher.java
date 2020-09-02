package net.thucydides.core.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import java.util.Collection;

public class DateTimeCollectionContainsSameDatesMatcher  extends TypeSafeMatcher<Collection<DateTime>> {
    private final Collection<DateTime> expectedDates;

    public DateTimeCollectionContainsSameDatesMatcher(final Collection<DateTime> expectedDates) {
        this.expectedDates = expectedDates;
    }

    @Override
    public boolean matchesSafely(Collection<DateTime> dates) {
        if (dates.size() != expectedDates.size()) {
            return false;
        }
        for (DateTime expectedDate : expectedDates) {
            if (!hasIdenticalDate(expectedDate, dates)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasIdenticalDate(DateTime expectedDate, Collection<DateTime> dates) {
        for (DateTime date : dates) {
            if (DateComparator.sameDate(date, expectedDate)) {
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
