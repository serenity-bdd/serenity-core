package serenitycore.net.serenitybdd.core.annotations.findby.di;

import org.openqa.selenium.By;

import java.lang.reflect.Field;

/**
 * Created by Sergio Sacristan on 03/12/17.
 *
 * Implement this Interface to add custom findBy annotations
 * @see  ClasspathCustomFindByAnnotationProviderService
 */
public interface CustomFindByAnnotationService {

    /**
     *  Returns true if the field has any custom annotation
     * @param field
     * @return true if the field has any custom annotation
     */
    boolean isAnnotatedByCustomFindByAnnotation(Field field);

    /**
     *  Retuns an org.openqa.selenium.By implementation
     *  that will be used to find the "field" by Selenium
     * @param field
     * @return an org.openqa.selenium.By implementation
     */
    By buildByFromCustomFindByAnnotation(Field field);
}
