package serenitycore.net.serenitybdd.core.annotations.findby.di;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by Sergio Sacristan on 03/12/17.
 *
 * Load any implementations of the CustomFindByAnnotationService class declared on the classpath.
 * CustomFindByAnnotationService implementations must be declared in a file called net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationService
 * in the META-INF/services directory somewhere on the classpath.
 */
public class ClasspathCustomFindByAnnotationProviderService implements CustomFindByAnnotationProviderService {

    private List<CustomFindByAnnotationService> findByAnnotationServices;

    @Override
    public List<CustomFindByAnnotationService> getCustomFindByAnnotationServices() {
        if (findByAnnotationServices == null) {
            findByAnnotationServices = new ArrayList<>();

            ServiceLoader<CustomFindByAnnotationService> seleniumAnnotationServiceLoader = ServiceLoader.load(CustomFindByAnnotationService.class);

            for (CustomFindByAnnotationService findByAnnotationService : seleniumAnnotationServiceLoader) {
                findByAnnotationServices.add(findByAnnotationService);
            }
        }
        return findByAnnotationServices;
    }
}
