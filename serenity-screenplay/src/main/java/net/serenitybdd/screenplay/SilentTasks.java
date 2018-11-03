package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.markers.IsSilent;

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
}
