package net.thucydides.core.statistics.service;


import java.util.ArrayList;
import java.util.List;

public class JUnitCleanupMethodAnnotationProvider implements CleanupMethodAnnotationProvider {

    @Override
    public List<String> getCleanupMethodAnnotations() {
        List<String> cleanupAnnotationTags = new ArrayList<>();
        cleanupAnnotationTags.add("@org.junit.After()");
        cleanupAnnotationTags.add("@org.junit.AfterClass()");
        return cleanupAnnotationTags;
    }
}
