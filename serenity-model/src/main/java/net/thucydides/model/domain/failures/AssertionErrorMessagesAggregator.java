package net.thucydides.model.domain.failures;

import java.util.List;

public class AssertionErrorMessagesAggregator {
    public AssertionErrorMessagesAggregator() {
    }

    public static String aggregateErrorMessages(List<String> errors) {
        StringBuilder msg = new StringBuilder(System.lineSeparator() + "The following ");
        countAssertions(errors, msg);
        msg.append(" failed:").append(System.lineSeparator());

        for (int i = 0; i < errors.size(); ++i) {
            String leadingSpaces = " ".repeat(("" + (i + 1) + ") ").length());
            msg.append(i + 1)
                    .append(") ")
                    .append(stripLeadingLineBreakFrom(errors.get(i)).replaceFirst("Actual",leadingSpaces + "Actual"))
                    .append(System.lineSeparator());
        }

        return msg.toString();
    }

    private static String stripLeadingLineBreakFrom(String errorMessage) {
        return (errorMessage != null) ? errorMessage.replaceFirst("^(\\r\\n|\\n|\\r)", "") : "";
    }

    private static void countAssertions(List<String> errors, StringBuilder msg) {
        int size = errors.size();
        if (size == 1) {
            msg.append("assertion");
        } else {
            msg.append(size).append(" assertions");
        }
    }
}
