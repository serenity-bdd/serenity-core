package serenitycore.net.thucydides.core.steps.service;


import java.util.List;

/**
 * Specifies the annotations of the cleanup methods.
 * For example,for JUnit they are : @org.junit.After(), @org.junit.AfterClass()
 */
public interface CleanupMethodAnnotationProvider {

    public List<String> getCleanupMethodAnnotations();
}
