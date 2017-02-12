package net.serenitybdd.junit.runners;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement(name="rerunnableClasses")
@XmlAccessorType(XmlAccessType.FIELD)
public class RerunnableClasses {

    @XmlElement(name = "rerunnableClass")
    Set<RerunnableClass> rerunnableClasses;

    public void setRerunnableClasses(Set<RerunnableClass> allRerunnableClasses) {
        rerunnableClasses = allRerunnableClasses;
    }

    public Set<RerunnableClass> getRerunnableClasses() {
        return rerunnableClasses;
    }

    public RerunnableClass getRerunnableClassWithName(String name){
        if(rerunnableClasses == null) {
            return null;
        }
        for(RerunnableClass rerunnableClass : rerunnableClasses) {
            if(rerunnableClass.getClassName().equals(name)){
                return rerunnableClass;
            }
        }
        return null;
    }
}
