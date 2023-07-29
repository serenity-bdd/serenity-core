package net.thucydides.core.reports;

import com.google.common.base.Splitter;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FormatConfiguration {


    private final static Logger LOGGER = LoggerFactory.getLogger(FormatConfiguration.class);

    private final static String DEFAULT_FORMATS = "json";

    private final List<OutcomeFormat> formats;

    
    public FormatConfiguration(EnvironmentVariables environmentVariables) {
        List<String> formatNames = Splitter.on(",").trimResults()
                                                   .splitToList(ThucydidesSystemProperty.OUTPUT_FORMATS
                                                           .from(environmentVariables, DEFAULT_FORMATS));

        formats = new ArrayList<>();
        for(String format : formatNames) {
            formats.addAll(outcomeFormatFrom(format).map(Collections::singleton).orElse(Collections.emptySet()));
        }
    }

    FormatConfiguration(OutcomeFormat... formatValues) {
        formats = Arrays.asList(formatValues);
    }

    private static Optional<OutcomeFormat> outcomeFormatFrom(String value) {
        try {
            return Optional.of(OutcomeFormat.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Illegal outcome format ignored: " + value);
            return Optional.empty();
        }
    }

    public List<OutcomeFormat> getFormats() {
        if (formats.isEmpty()) {
            throw new IllegalArgumentException("No valid output format has been defined.");
        }
        return formats;
    }

    public OutcomeFormat getPreferredFormat() {
        return getFormats().get(0);
    }
}
