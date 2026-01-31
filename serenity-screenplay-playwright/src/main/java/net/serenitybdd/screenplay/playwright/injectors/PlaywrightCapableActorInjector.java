package net.serenitybdd.screenplay.playwright.injectors;

import net.serenitybdd.annotations.Fields;
import net.serenitybdd.model.di.DependencyInjector;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.annotations.PlaywrightCastMember;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * Dependency injector that creates actors with Playwright browser capabilities
 * based on the {@link PlaywrightCastMember} annotation.
 *
 * <p>This injector is registered via SPI and is automatically invoked by the
 * Serenity test framework when running tests.</p>
 */
public class PlaywrightCapableActorInjector implements DependencyInjector {

    @Override
    public void injectDependenciesInto(Object object) {
        if (object == null) {
            return;
        }
        Fields.of(object.getClass())
            .fieldsAnnotatedBy(PlaywrightCastMember.class)
            .forEach(field -> injectActor(field, object));
    }

    private void injectActor(Field field, Object object) {
        PlaywrightCastMember castMember = field.getAnnotation(PlaywrightCastMember.class);
        String name = castMember.name().isEmpty()
            ? StringUtils.capitalize(field.getName())
            : castMember.name();

        if (!OnStage.theStageIsSet()) {
            OnStage.setTheStage(Cast.ofStandardActors());
        }

        try {
            Actor actor = OnStage.theActorCalled(name);

            // Create and configure the Playwright ability
            BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.usingTheDefaultConfiguration()
                .withBrowserType(castMember.browserType())
                .withHeadlessMode(castMember.headless());

            actor.can(ability);

            field.setAccessible(true);
            field.set(object, actor);
        } catch (IllegalAccessException e) {
            throw new CastingException("Unable to instantiate actor " + name, e);
        }
    }

    @Override
    public void reset() {
        // No state to reset
    }
}