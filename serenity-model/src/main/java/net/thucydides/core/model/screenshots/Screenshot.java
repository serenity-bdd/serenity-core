package net.thucydides.core.model.screenshots;

import net.thucydides.core.model.ErrorMessageFormatter;
import net.thucydides.core.model.stacktrace.FailureCause;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Objects;

/**
 * Represents a screenshot stored during a test execution.
 */
public class Screenshot {
    private final String filename;
    private final String description;
    private final int width;
    private final FailureCause error;
    private final Long timestamp;

    public Screenshot(final String filename,
                      final String description,
                      final int width,
                      final long timestamp,
                      final FailureCause error) {
        this.filename = filename;
        this.description = description;
        this.timestamp = timestamp;
        this.width = width;
        this.error = error;
    }

    public Screenshot(final String filename,
                      final String description,
                      final int width,
                      final long timestamp) {
        this(filename, description, width, timestamp, null);
    }

    public FailureCause getError() {
        return error;
    }

    public String getErrorMessage() {
        return (error != null) ? error.getMessage(): "";
    }

    /**
     * Returns the first line only of the error message.
     * This avoids polluting the UI with unnecessary details such as browser versions and so forth.
     */
    public String getShortErrorMessage() {
        return new ErrorMessageFormatter(getErrorMessage()).getShortErrorMessage();
    }

    public String getFilename() {
        return filename;
    }

    public String getDescription() {
        return description;
    }

    public int getWidth() {
        return width;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public HtmlFormattedInfo getHtml() {
        return new HtmlFormattedInfo(description);
    }

    public Screenshot withDescription(String description) {
        return new Screenshot(filename, description, width, timestamp);
    }

    public Screenshot before() {
        return new Screenshot(filename, description, width, timestamp  - 1);
    }

    public static class HtmlFormattedInfo {
        private final String description;

        public HtmlFormattedInfo(String description) {
            this.description = description;
        }

        public String getDescription() {
            return StringEscapeUtils.escapeHtml4(description);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screenshot that = (Screenshot) o;
        return Objects.equals(filename, that.filename) &&
                Objects.equals(description, that.description) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, description, timestamp);
    }
}
