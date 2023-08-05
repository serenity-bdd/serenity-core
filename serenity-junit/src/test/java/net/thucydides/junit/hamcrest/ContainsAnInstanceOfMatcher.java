package net.thucydides.junit.hamcrest;

import net.thucydides.model.reports.AcceptanceTestReporter;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class ContainsAnInstanceOfMatcher extends TypeSafeMatcher<List<AcceptanceTestReporter>> {
    
    private final Class<? extends AcceptanceTestReporter> reporterClass;
    
    public ContainsAnInstanceOfMatcher(Class<? extends AcceptanceTestReporter> reporterClass) {
        this.reporterClass = reporterClass;
    }

    public boolean matchesSafely(List<AcceptanceTestReporter> reporters) {
        
        boolean matchingReporterFound = false;
        for(AcceptanceTestReporter reporter : reporters) {
            if (thisIsTheRightSortOf(reporter)){
                matchingReporterFound = true;
                break;
            }
        }
        return matchingReporterFound;
    }

    private boolean thisIsTheRightSortOf(AcceptanceTestReporter reporter) {
        return reporter.getClass().isAssignableFrom(reporterClass);
    }

    public void describeTo(Description description) {
        description.appendText("using an ").appendText(reporterClass.getName()).appendText(" reporter");
    }
}
