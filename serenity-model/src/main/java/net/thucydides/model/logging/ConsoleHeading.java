package net.thucydides.model.logging;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.util.EnvironmentVariables;
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
                        "\n  _____   ___   ___   _____     ___   _____     _     ___   _____   ___   ___  \n" +
                                " |_   _| | __| / __| |_   _|   / __| |_   _|   /_\\   | _ \\ |_   _| | __| |   \\ \n" +
                                "   | |   | _|  \\__ \\   | |     \\__ \\   | |    / _ \\  |   /   | |   | _|  | |) |\n" +
                                "   |_|   |___| |___/   |_|     |___/   |_|   /_/ \\_\\ |_|_\\   |_|   |___| |___/ \n"
                ));

        HEADINGS.put(ConsoleEvent.TEST_PASSED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_PASSED),
                        "\n  _____   ___   ___   _____     ___     _     ___   ___   ___   ___  \n" +
                                " |_   _| | __| / __| |_   _|   | _ \\   /_\\   / __| / __| | __| |   \\ \n" +
                                "   | |   | _|  \\__ \\   | |     |  _/  / _ \\  \\__ \\ \\__ \\ | _|  | |) |\n" +
                                "   |_|   |___| |___/   |_|     |_|   /_/ \\_\\ |___/ |___/ |___| |___/ \n"
                ));

        HEADINGS.put(ConsoleEvent.TEST_FAILED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_FAILED),
                        "\n  _____   ___   ___   _____     ___     _     ___   _      ___   ___  \n" +
                                " |_   _| | __| / __| |_   _|   | __|   /_\\   |_ _| | |    | __| |   \\ \n" +
                                "   | |   | _|  \\__ \\   | |     | _|   / _ \\   | |  | |__  | _|  | |) |\n" +
                                "   |_|   |___| |___/   |_|     |_|   /_/ \\_\\ |___| |____| |___| |___/ \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_ERROR,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_ERROR),
                        "\n  _____   ___   ___   _____     ___   ___   ___    ___    ___ \n" +
                                " |_   _| | __| / __| |_   _|   | __| | _ \\ | _ \\  / _ \\  | _ \\\n" +
                                "   | |   | _|  \\__ \\   | |     | _|  |   / |   / | (_) | |   /\n" +
                                "   |_|   |___| |___/   |_|     |___| |_|_\\ |_|_\\  \\___/  |_|_\\\n")
        );

        HEADINGS.put(ConsoleEvent.TEST_COMPROMISED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_COMPROMISED),
                        "\n  _____   ___   ___   _____ \n" +
                                " |_   _| | __| / __| |_   _|\n" +
                                "   | |   | _|  \\__ \\   | |  \n" +
                                "   |_|   |___| |___/   |_|  \n" +
                                "   ___    ___    __  __   ___   ___    ___    __  __   ___   ___   ___   ___  \n" +
                                "  / __|  / _ \\  |  \\/  | | _ \\ | _ \\  / _ \\  |  \\/  | |_ _| / __| | __| |   \\ \n" +
                                " | (__  | (_) | | |\\/| | |  _/ |   / | (_) | | |\\/| |  | |  \\__ \\ | _|  | |) |\n" +
                                "  \\___|  \\___/  |_|  |_| |_|   |_|_\\  \\___/  |_|  |_| |___| |___/ |___| |___/ \n")
        );


        HEADINGS.put(ConsoleEvent.TEST_SKIPPED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_COMPROMISED),
                        "\n  _____   ___   ___   _____     ___   _  __  ___   ___   ___   ___   ___  \n" +
                        " |_   _| | __| / __| |_   _|   / __| | |/ / |_ _| | _ \\ | _ \\ | __| |   \\ \n" +
                        "   | |   | _|  \\__ \\   | |     \\__ \\ | ' <   | |  |  _/ |  _/ | _|  | |) |\n" +
                        "   |_|   |___| |___/   |_|     |___/ |_|\\_\\ |___| |_|   |_|   |___| |___/ \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_ABORTED,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_COMPROMISED),
                        "\n  _____   ___   ___   _____     ___   _  __  ___   ___   ___   ___   ___  \n" +
                                " |_   _| | __| / __| |_   _|   / __| | |/ / |_ _| | _ \\ | _ \\ | __| |   \\ \n" +
                                "   | |   | _|  \\__ \\   | |     \\__ \\ | ' <   | |  |  _/ |  _/ | _|  | |) |\n" +
                                "   |_|   |___| |___/   |_|     |___/ |_|\\_\\ |___| |_|   |_|   |___| |___/ \n")
        );

        HEADINGS.put(ConsoleEvent.TEST_PENDING,
                NewList.of(
                        "",
                        titleWithBorder(ConsoleEvent.TEST_PENDING),
                        "\n  _____   ___   ___   _____     ___   ___   _  _   ___    ___   _  _    ___ \n" +
                                " |_   _| | __| / __| |_   _|   | _ \\ | __| | \\| | |   \\  |_ _| | \\| |  / __|\n" +
                                "   | |   | _|  \\__ \\   | |     |  _/ | _|  | .` | | |) |  | |  | .` | | (_ |\n" +
                                "   |_|   |___| |___/   |_|     |_|   |___| |_|\\_| |___/  |___| |_|\\_|  \\___|\n")
        );

    }

    static String titleWithBorder(ConsoleEvent consoleEvent) {
//        String title = "- " + consoleEvent.getTitle().toUpperCase() + " -";
//        String border = StringUtils.repeat("-", title.length());
//        return border + "\n" + title + "\n" + border + "\n";

        return "- " + consoleEvent.getTitle().toUpperCase();
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
