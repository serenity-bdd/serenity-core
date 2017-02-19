package net.serenitybdd.junit.runners;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name="rerunnableClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class RerunnableClass {

    String className;

    @XmlElement(name="methodName")
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
