package net.thucydides.model.reports.history;

import org.joda.time.DateTime;

public interface DateProvider {

    DateTime getCurrentTime();
}
