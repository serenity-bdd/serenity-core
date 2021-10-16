package net.serenitybdd.screenplay.injectors;

import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Managed;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WebCapableActorInjector implements DependencyInjector {
    @Override
    public void injectDependenciesInto(Object object) {
        List<Field> actorFields = Fields.of(object.getClass()).fieldsAnnotatedBy(CastMember.class);
        actorFields.forEach(field -> injectActor(field, object));
    }

    private void injectActor(Field field, Object object) {
        CastMember castMember = field.getAnnotation(CastMember.class);
        String name = castMember.name();
        if (name.isEmpty()) {
            name = StringUtils.capitalize(field.getName());
        }
        Actor actor = Actor.named(name);

        try {
            browserFor(object, castMember).ifPresent(
                browser -> actor.can(BrowseTheWeb.with(browser))
            );
            field.setAccessible(true);
            field.set(object, actor);
        } catch (IllegalAccessException e) {
            throw new CastingException("Unable to instantiate actor " + name, e);
        }
    }

    private Optional<WebDriver> browserFor(Object object, CastMember castMember) throws IllegalAccessException {
        List<Field> browserFields = Fields.of(object.getClass()).fieldsAnnotatedBy(Managed.class);
        if (browserFields.isEmpty()) {
            return Optional.empty();
        }
        String browserName = castMember.browser();
        if (browserName.isEmpty()) {
            return Optional.of(driverInField(browserFields.get(0), object));
        }

        return browserFields.stream()
                .filter(field -> field.getName().equals(browserName))
                .map(field -> driverInField(field, object))
                .findFirst();
    }

    private WebDriver driverInField(Field field, Object object) {
        try {
            field.setAccessible(true);
            return (WebDriver) field.get(object);
        } catch (IllegalAccessException e) {
            throw new CastingException("Unable to instantiate actor with webdriver for field " + field.getName(), e);
        }
    }

    @Override
    public void reset() {
    }
}
