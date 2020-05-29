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
                        "\n___ ____ ____ ___    ____ ___ ____ ____ ___ ____ ___  \n" +
                                " |  |___ [__   |     [__   |  |__| |__/  |  |___ |  \\ \n" +
                                " |  |___ ___]  |     ___]  |  |  | |  \\  |  |___ |__/ \n" +
                                "----------------------------------------------------",
                        "\n _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                                "|_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                                "  | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                                "  | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                                "  |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n"));

        HEADINGS.put(ConsoleEvent.TEST_PASSED,
                NewList.of(
                        "",
                        "\n___ ____ ____ ___    ___  ____ ____ ____ ____ ___  \n" +
                                " |  |___ [__   |     |__] |__| [__  [__  |___ |  \\ \n" +
                                " |  |___ ___]  |     |    |  | ___] ___] |___ |__/ \n" +
                                "-------------------------------------------------",
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
                        "\n___ ____ ____ ___    ____ ____ _ _    ____ ___  \n" +
                                " |  |___ [__   |     |___ |__| | |    |___ |  \\ \n" +
                                " |  |___ ___]  |     |    |  | | |___ |___ |__/ \n" +
                                " ---------------------------------------------",
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
                        "\n___ ____ ____ ___    ____ ____ ____ ____ ____ \n" +
                                " |  |___ [__   |     |___ |__/ |__/ |  | |__/ \n" +
                                " |  |___ ___]  |     |___ |  \\ |  \\ |__| |  \\ \n" +
                                "---------------------------------------------",
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
                        "\n___ ____ ____ ___    ____ ____ _  _ ___  ____ ____ _  _ _ ____ ____ ___  \n" +
                                " |  |___ [__   |     |    |  | |\\/| |__] |__/ |  | |\\/| | [__  |___ |  \\ \n" +
                                " |  |___ ___]  |     |___ |__| |  | |    |  \\ |__| |  | | ___] |___ |__/ \n" +
                                "-----------------------------------------------------------------------",
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
                        "\n___ ____ ____ ___    ____ _  _ _ ___  ___  ____ ___  \n" +
                                " |  |___ [__   |     [__  |_/  | |__] |__] |___ |  \\ \n" +
                                " |  |___ ___]  |     ___] | \\_ | |    |    |___ |__/ \n" +
                                "---------------------------------------------------",
                        "\n            __  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                                "  _        / / |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                                " (_)_____ / /    | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                                "  _|_____/ /     | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                                " (_)    /_/      |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_PENDING,
                NewList.of(
                        "",
                        "\n___ ____ ____ ___    ___  ____ _  _ ___  _ _  _ ____ \n" +
                                " |  |___ [__   |     |__] |___ |\\ | |  \\ | |\\ | | __ \n" +
                                " |  |___ ___]  |     |    |___ | \\| |__/ | | \\| |__] \n" +
                                "----------------------------------------------------",
                        "\n          __  _____ _____ ____ _____   ____  _____ _   _ ____ ___ _   _  ____ \n" +
                                " _       / / |_   _| ____/ ___|_   _| |  _ \\| ____| \\ | |  _ \\_ _| \\ | |/ ___|\n" +
                                "(_)____ / /    | | |  _| \\___ \\ | |   | |_) |  _| |  \\| | | | | ||  \\| | |  _ \n" +
                                " |_____/ /     | | | |___ ___) || |   |  __/| |___| |\\  | |_| | || |\\  | |_| |\n" +
                                "(_)   /_/      |_| |_____|____/ |_|   |_|   |_____|_| \\_|____/___|_| \\_|\\____|\n")
        );

    }

    private final EnvironmentVariables environmentVariables;
    private final ConsoleHeadingStyle headingStyle;
    private final ConsoleHeadingStyle bannerStyle;

    public ConsoleHeading(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.headingStyle = ConsoleHeadingStyle.definedIn(environmentVariables);
        this.bannerStyle = ConsoleHeadingStyle.bannerStyleDefinedIn(environmentVariables);
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
