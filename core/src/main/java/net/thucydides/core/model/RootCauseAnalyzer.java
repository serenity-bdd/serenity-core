package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.SIMPLIFIED_STACK_TRACES;

/**
 * Created by john on 3/07/2014.
 */
public class RootCauseAnalyzer {

    private final Throwable thrownException;

    private final EnvironmentVariables environmentVariables;

    public RootCauseAnalyzer(Throwable thrownException) {
        this(thrownException, Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public RootCauseAnalyzer(Throwable thrownException, EnvironmentVariables environmentVariables) {
        this.thrownException = thrownException;
        this.environmentVariables = environmentVariables;
    }

    public FailureCause getRootCause() {
            Throwable originalException = (thrownException.getCause() != null)? thrownException.getCause() : thrownException;
            return new FailureCause(originalException, sanitized(originalException.getStackTrace()));
    }

    public String getClassname() {
        return getRootCause().getErrorType();
    }

    public String getMessage() {
        return getRootCause().getMessage();
    }

    private StackTraceElement[] sanitized(StackTraceElement[] stackTrace) {
        return useSimplifedStackTraces() ? simplifiedStackTrace(stackTrace) : stackTrace;
    }

    private boolean useSimplifedStackTraces() {
        return environmentVariables.getPropertyAsBoolean(SIMPLIFIED_STACK_TRACES,true);
    }

    private StackTraceElement[] simplifiedStackTrace(StackTraceElement[] stackTrace) {
        List<StackTraceElement> cleanStackTrace = Lists.newArrayList();
        for(StackTraceElement element : stackTrace) {
            if (shouldDisplayInStackTrace(element)) {
                cleanStackTrace.add(element);
            }
        }
        return cleanStackTrace.toArray(new StackTraceElement[0]);
    }


    private final static List<String> MASKED_PACKAGES = ImmutableList.of(
            "sun.",
            "com.sun",
            "java.",
            "org.junit",
            "org.gradle",
            "org.fest",
            "org.hamcrest",
            "org.openqa.selenium",
            "org.spockframework",
            "org.apache.maven.surefire",
            "com.intellij",
            "net.sf.cglib",
            "org.codehaus.groovy",
            "org.jbehave",
            "net.thucydides.core",
            "net.thucydides.jbehave",
            "net.thucydides.junit");

    private boolean shouldDisplayInStackTrace(StackTraceElement element) {
        if (element.getClassName().contains("$$")) {
            return false;
        }
        for(String maskedPackage : MASKED_PACKAGES) {
            if (element.getClassName().startsWith(maskedPackage)) {
                return false;
            }
        }
        return true;
    }
}
