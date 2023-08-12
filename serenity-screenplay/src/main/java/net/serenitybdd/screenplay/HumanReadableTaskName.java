package net.serenitybdd.screenplay;

import net.thucydides.model.util.NameConverter;

public class HumanReadableTaskName {

    public static String forCurrentMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int businessIndex = 1;
        for(int methodIndex = 1; methodIndex < stackTrace.length; methodIndex++) {
            if (!stackTrace[methodIndex].getClassName().startsWith("net.serenitybdd")) {
                businessIndex = methodIndex;
                break;
            }
        }
        String className = Thread.currentThread().getStackTrace()[businessIndex].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[businessIndex].getMethodName();
        int lastDot = className.lastIndexOf(".");
        String simpleClassName = (lastDot == -1) ? className : className.substring(className.lastIndexOf(".") + 1);
        return NameConverter.humanize(simpleClassName + "_" + methodName);
    }
}
