package net.thucydides.core.logging;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleHeading {

    private static Map<ConsoleEvent, List<String>> HEADINGS = new HashMap<>();
    static {
        HEADINGS.put(ConsoleEvent.TEST_STARTED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_STARTED),
                        "\n _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                                "|_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                                "  | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                                "  | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                                "  |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n"));

        HEADINGS.put(ConsoleEvent.TEST_PASSED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_PASSED),
                        "\n        __    _____ _____ ____ _____   ____   _    ____  ____  _____ ____  \n" +
                                "  _     \\ \\  |_   _| ____/ ___|_   _| |  _ \\ / \\  / ___|/ ___|| ____|  _ \\ \n" +
                                " (_)_____| |   | | |  _| \\___ \\ | |   | |_) / _ \\ \\___ \\\\___ \\|  _| | | | |\n" +
                                "  _|_____| |   | | | |___ ___) || |   |  __/ ___ \\ ___) |___) | |___| |_| |\n" +
                                " (_)     | |   |_| |_____|____/ |_|   |_| /_/   \\_\\____/|____/|_____|____/ \n" +
                                "        /_/                                                                \n")
                );

        HEADINGS.put(ConsoleEvent.TEST_FAILED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_FAILED),
                        "\n           __  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
                                "  _       / / |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
                                " (_)_____| |    | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
                                "  _|_____| |    | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
                                " (_)     | |    |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
                                "          \\_\\                                                            \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_ERROR,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_ERROR),
                        "\n         __  _____ _____ ____ _____   _____ ____  ____   ___  ____  \n" +
                                " _      / / |_   _| ____/ ___|_   _| | ____|  _ \\|  _ \\ / _ \\|  _ \\ \n" +
                                "(_)____| |    | | |  _| \\___ \\ | |   |  _| | |_) | |_) | | | | |_) |\n" +
                                " |_____| |    | | | |___ ___) || |   | |___|  _ <|  _ <| |_| |  _ < \n" +
                                "(_)    | |    |_| |_____|____/ |_|   |_____|_| \\_\\_| \\_\\\\___/|_| \\_\\\n" +
                                "        \\_\\                                                         \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_COMPROMISED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_COMPROMISED),
                        "\n         __  _____ _____ ____ _____ \n" +
                                " _      / / |_   _| ____/ ___|_   _|\n" +
                                "(_)____| |    | | |  _| \\___ \\ | |  \n" +
                                " |_____| |    | | | |___ ___) || |  \n" +
                                "(_)    | |    |_| |_____|____/ |_|  \n" +
                                "        \\_\\                         \n" +
                                "  ____ ___  __  __ ____  ____   ___  __  __ ___ ____  _____ ____  \n" +
                                " / ___/ _ \\|  \\/  |  _ \\|  _ \\ / _ \\|  \\/  |_ _/ ___|| ____|  _ \\ \n" +
                                "| |  | | | | |\\/| | |_) | |_) | | | | |\\/| || |\\___ \\|  _| | | | |\n" +
                                "| |__| |_| | |  | |  __/|  _ <| |_| | |  | || | ___) | |___| |_| |\n" +
                                " \\____\\___/|_|  |_|_|   |_| \\_\\\\___/|_|  |_|___|____/|_____|____/\n")
        );


        HEADINGS.put(ConsoleEvent.TEST_SKIPPED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_COMPROMISED),
                        "\n            __  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                                "  _        / / |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                                " (_)_____ / /    | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                                "  _|_____/ /     | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                                " (_)    /_/      |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_PENDING,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_PENDING),
                        "\n          __  _____ _____ ____ _____   ____  _____ _   _ ____ ___ _   _  ____ \n" +
                                " _       / / |_   _| ____/ ___|_   _| |  _ \\| ____| \\ | |  _ \\_ _| \\ | |/ ___|\n" +
                                "(_)____ / /    | | |  _| \\___ \\ | |   | |_) |  _| |  \\| | | | | ||  \\| | |  _ \n" +
                                " |_____/ /     | | | |___ ___) || |   |  __/| |___| |\\  | |_| | || |\\  | |_| |\n" +
                                "(_)   /_/      |_| |_____|____/ |_|   |_|   |_____|_| \\_|____/___|_| \\_|\\____|\n")
        );

    }

    static String titleWithBorder(ConsoleEvent consoleEvent) {
        String title = "- " + consoleEvent.getTitle().toUpperCase() + " -";
        String border = StringUtils.repeat("-", title.length());
        return border + "\n" + title + "\n" + border + "\n";
    }

    private final ConsoleHeadingStyle headingStyle;

    public ConsoleHeading(EnvironmentVariables environmentVariables) {
        this.headingStyle = ConsoleHeadingStyle.definedIn(environmentVariables);
    }


    public String bannerFor(ConsoleEvent consoleEvent, String description) {
        String heading = headingFor(consoleEvent);
        String eventPrefix = eventPrefix(consoleEvent.getTitle());
        return heading + eventPrefix + description + underline(heading);
    }

    private String headingFor(ConsoleEvent consoleEvent) {
        return HEADINGS.get(consoleEvent).get(headingStyle.getLevel());
    }


    private String eventPrefix(String prefix) {
        if (headingStyle.equals(ConsoleHeadingStyle.NONE)) {
            return prefix + ": ";
        } else {
            return "\n";
        }
    }

    private String underline(String banner) {
        StringBuilder underline = new StringBuilder();
        int endOfLine = banner.indexOf('\n', 1);
        if (endOfLine >= 0) {
            underline.append(StringUtils.repeat('-', endOfLine));
        } else {
            underline.append(StringUtils.repeat('-', banner.length()));
        }
        return (!underline.toString().isEmpty()) ? "\n" + underline.toString() : "";
    }
}
