package net.serenitybdd.junit.runners;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;

public class RerunnableClass {

    @JsonProperty
    String className;

    @JsonProperty("methodName")
    Set<String> methodNames = new HashSet<>();

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(Set<String> methodNames) {
        this.methodNames = methodNames;
    }
}
