package net.thucydides.model.steps;

import org.apache.commons.lang3.ArrayUtils;

public class StepArgumentWriter {

    public static String readableFormOf(Object arg) {
        if (arg == null) {
            return "<null>";
        } else if (arg.getClass().isArray()) {
            return ArrayUtils.toString(arg);
        } else {
            try {
                return arg.toString();
            } catch (Throwable e) {
                return "<" + arg.getClass().getName() + ">";
            }
        }
    }
}
