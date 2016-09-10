package net.thucydides.core.model.screenshots;

import net.thucydides.core.model.ErrorMessageFormatter;
import net.thucydides.core.model.stacktrace.FailureCause;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Represents a screenshot stored during a test execution.
 */
public class Screenshot {
    private final String filename;
    private final String description;
    private final int width;
    private final FailureCause error;

    public Screenshot(final String filename,
                      final String description,
                      final int width,
                      final FailureCause error) {
        this.filename = filename;
        this.description = description;
        this.width = width;
        this.error = error;
    }

    public Screenshot(final String filename,
                      final String description,
                      final int width) {
        this(filename, description, width, null);
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

    public HtmlFormattedInfo getHtml() {
        return new HtmlFormattedInfo(description);
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
}
