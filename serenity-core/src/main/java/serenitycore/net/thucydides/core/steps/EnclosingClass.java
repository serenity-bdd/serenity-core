package serenitycore.net.thucydides.core.steps;

import serenitycore.net.thucydides.core.annotations.InvalidStepsFieldException;

public class EnclosingClass {

    private Class<?> innerClass;

    public EnclosingClass(Class<?> innerClass) {
        this.innerClass = innerClass;
    }

    public static EnclosingClass of(Class<?> innerClass) {
        return new EnclosingClass(innerClass);
    }

    public Object newInstance() {
        try {
            return innerClass.getEnclosingClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidStepsFieldException("Please ensure that there is a default constructor for " + innerClass.getEnclosingClass());
        }
    }

    public Object[] asParameters() {
        return new Object[] { newInstance() };
    }
}
