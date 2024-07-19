package net.thucydides.model.domain.screenshots;

import net.thucydides.model.domain.ErrorMessageFormatter;
import net.thucydides.model.domain.stacktrace.FailureCause;
import org.apache.commons.text.StringEscapeUtils;

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
    private final int depth;

    public Screenshot(String filename,
                      String description,
                      int width,
                      long timestamp,
                      FailureCause error) {
        this(filename,description, width, timestamp, error, 0);
    }


    public Screenshot(String filename,
                      String description,
                      int width,
                      long timestamp,
                      FailureCause error,
                      int depth) {
        this.filename = filename;
        this.description = description;
        this.timestamp = timestamp;
        this.width = width;
        this.error = error;
        this.depth = depth;
    }

    public Screenshot(final String filename,
                      final String description,
                      final int width,
                      final long timestamp) {
        this(filename, description, width, timestamp, null, 0);
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

    public int getDepth() {
        return depth;
    }

    public HtmlFormattedInfo getHtml() {
        return new HtmlFormattedInfo(description);
    }

    public Screenshot withDescription(String description) {
        return new Screenshot(filename, description, width, timestamp, error, depth);
    }

    public Screenshot withDepth(int depth) {
        return new Screenshot(filename, description, width, timestamp, error, depth);
    }

    public Screenshot before() {
        return new Screenshot(filename, description, width, timestamp  - 1, error, depth);
    }

    public static class HtmlFormattedInfo {
        private final String description;

        HtmlFormattedInfo(String description) {
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
