package net.thucydides.model.requirements.classpath;

/**
 * Created by john on 20/07/2016.
 */
public class PackageInfoClass {
    public static boolean isDefinedIn(String path) {
        return path.toLowerCase().endsWith("package-info");
    }
}
