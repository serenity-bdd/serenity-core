package serenitycore.net.serenitybdd.core.annotations.findby.di

import org.openqa.selenium.By
import serenitycore.net.serenitybdd.core.annotations.findby.di.ClasspathCustomFindByAnnotationProviderService
import serenitycore.net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationService
import spock.lang.Specification

import java.lang.reflect.Field

class ClasspathCustomFindByAnnotationProviderServiceTest extends Specification {

    class RocheFindByAnnotationService implements CustomFindByAnnotationService {

        boolean isAnnotatedByCustomFindByAnnotation(Field field){
          return true;
        }

        By buildByFromCustomFindByAnnotation(Field field){
          return null;
        }
    }

    def "GetCustomFindByAnnotationServices"() {
        given:
        ClasspathCustomFindByAnnotationProviderService classpathCustomFindByAnnotationProviderService = new ClasspathCustomFindByAnnotationProviderService();

        when:
        def result = classpathCustomFindByAnnotationProviderService.getCustomFindByAnnotationServices()

        then:
        result.size()==1
    }
}
