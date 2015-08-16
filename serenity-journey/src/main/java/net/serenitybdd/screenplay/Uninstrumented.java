package net.serenitybdd.screenplay;

/**
 * Created by john on 16/08/2015.
 */
public class Uninstrumented {

    @SuppressWarnings("unchecked")
    public static <T> Class<T> versionOf(Class<T> questionClass) {
        return questionClass.getName().contains("EnhancerByCGLIB") ? (Class<T>) questionClass.getSuperclass() : questionClass;
    }
}
