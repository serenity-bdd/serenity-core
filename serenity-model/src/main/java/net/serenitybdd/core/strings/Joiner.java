package net.serenitybdd.core.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Joiner {
    private String separator;
    private boolean skipNuls = false;

    public Joiner(String separator) {
        this.separator = separator;
    }

    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    public String join(List<String> values) {
        return values.stream().filter(this::shouldInclude).collect(Collectors.joining(separator));
    }

    private boolean shouldInclude(String value) {
        if ((skipNuls) && (value == null)) return false;
        return true;
    }

    public String join(String... values) {
        return Arrays.stream(values).filter(this::shouldInclude).collect(Collectors.joining(separator));
    }

    public Joiner skipNulls() {
        this.skipNuls = true;
        return this;
    }

    public final String join(Iterable<?> parts) {
        List<String> elements = new ArrayList<>();
        parts.forEach(
                part -> elements.add(part.toString())
        );
        return this.join(parts);
    }

}
