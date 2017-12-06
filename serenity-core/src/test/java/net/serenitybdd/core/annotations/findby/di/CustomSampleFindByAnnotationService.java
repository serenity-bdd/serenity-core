package net.serenitybdd.core.annotations.findby.di;

import org.openqa.selenium.By;

import java.lang.reflect.Field;

public class CustomSampleFindByAnnotationService implements CustomFindByAnnotationService {

    @Override
    public boolean isAnnotatedByCustomFindByAnnotation(Field field) {
        return false;
    }

    @Override
    public By buildByFromCustomFindByAnnotation(Field field) {
        return null;
    }
}
