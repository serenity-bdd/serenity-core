package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.markers.CanBeSilent;
import serenitycore.net.serenitybdd.markers.IsSilent;

import static java.util.Arrays.asList;

public class SilentTasks {
    public static boolean isSilent(Performable task) {
        if (task instanceof IsSilent) {
            return true;
        }

        if (task instanceof CanBeSilent) {
            return ((CanBeSilent) task).isSilent();
        }
        return false;
    }

    public static boolean isNestedInSilentTask() {
        return asList(new Exception().getStackTrace())
                .stream()
                .anyMatch(element -> element.getMethodName().equals("performSilently"));
    }
}
