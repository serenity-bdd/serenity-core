package net.thucydides.core.model;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a full WebDriver message into a shorter, more web-friendly format.
 */
public class ErrorMessageFormatter {
    private final Optional<String> originalMessage;
    Pattern LEADING_EXCEPTIONS = Pattern.compile("^<?([\\w]*\\.[\\w]*)+:");

    public ErrorMessageFormatter(String originalMessage) {
        this.originalMessage = Optional.ofNullable(originalMessage);
    }

    /**
     * Returns the first line only of the error message.
     * This avoids polluting the UI with unnecessary details such as browser versions and so forth.
     */
    public String getShortErrorMessage() {
        return escapedHtml((originalMessage.isPresent()) ? getUsefulMessageSummary() : "");
    }

    private String escapedHtml(String message) {
        return StringEscapeUtils.escapeHtml4(message);
    }

    private String getUsefulMessageSummary() {
        if (isHamcrestException()) {
            return compressedHamcrestMessage();
        } else {
            return extractFirstLine();
        }
    }

    private String compressedHamcrestMessage() {
        String messageWithoutExceptions = removeLeadingExceptionFrom(originalMessage.get());
        String words[] = StringUtils.split(messageWithoutExceptions);
        return StringUtils.join(words," ");
    }

    private String extractFirstLine() {
        String lines[] = originalMessage.get().split("\\r?\\n");
        return StringUtils.trimToEmpty(replaceDoubleQuotesIn(firstNonExceptionLineIn(lines)));
    }

    private String firstNonExceptionLineIn(String lines[]) {
        for(String candidateLine : lines) {
            String lineWithoutExceptions = removeLeadingExceptionFrom(candidateLine);
            if (StringUtils.isNotEmpty(lineWithoutExceptions)) {
                return lineWithoutExceptions;
            }
        }
        return "";
    }

    private String removeLeadingExceptionFrom(final String message) {
        Matcher matcher = LEADING_EXCEPTIONS.matcher(message);
        if (matcher.find()) {
            return matcher.replaceFirst("");
        } else {
            return message;
        }
    }

    private boolean isHamcrestException() {
        return originalMessage.get().contains("Expected:");
    }

    private String replaceDoubleQuotesIn(final String message) {
        return message.replaceAll("\"","'");
    }
}
