package net.serenitybdd.screenplay.injectors;

import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * Injects actors into the test, and gives them browsers where appropriate.
 */
public class WebCapableActorInjector implements DependencyInjector {
    @Override
    public void injectDependenciesInto(Object object) {
        Fields.of(object.getClass())
                .fieldsAnnotatedBy(CastMember.class)
                .forEach(field -> injectActor(field, object));
    }

    private void injectActor(Field field, Object object) {


        CastMember castMember = field.getAnnotation(CastMember.class);
        String name = castMember.name().isEmpty() ? StringUtils.capitalize(field.getName()) : castMember.name();

        if (!OnStage.theStageIsSet()) {
            OnStage.setTheStage(new OnlineCast());
        }

        try {
            Actor actor = OnStage.theActorCalled(name);
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

        //
        // If the actor doesn't want a browser, don't give them one.
        //
        if (!castMember.withAssignedBrowser()) {
            return Optional.empty();
        }

        //
        // If no browser field name is specified, we give the actor his or her own browser
        //
        if (castMember.browserField().isEmpty()) {
            return Optional.of(driverFor(castMember));
        }

        //
        // If the browser field name is specified, it must match a @Managed-annotated field in the class
        //
        Field matchingBrowserField = browserFieldCalled(browserFields, castMember.browserField(), object)
                .orElseThrow(() -> new IllegalArgumentException("Could not instantiate the actor " + castMember.name() + ": the browserField attribute was specified but no @Managed field called '" + castMember.browserField() + "' was found in this class."));

        return Optional.of(driverInField(matchingBrowserField, object));

    }

    private WebDriver driverFor(CastMember castMember) {
        WebDriver driver;
        if (castMember.driver().isEmpty()) {
            driver = ThucydidesWebDriverSupport.getWebdriverManager().getWebdriverByName(castMember.name());
        } else {
            driver = ThucydidesWebDriverSupport.getWebdriverManager()
                    .withOptions(castMember.options())
                    .getWebdriverByName(castMember.name(), castMember.driver());
        }
        if ((driver instanceof WebDriverFacade) && (!StepEventBus.getEventBus().isASingleBrowserScenario())) {
            ((WebDriverFacade) driver).reset();
        }

        return driver;
    }

    private Optional<Field> browserFieldCalled(List<Field> browserFields, String browserFieldName, Object object) {
        return browserFields.stream()
                .filter(field -> field.getName().equals(browserFieldName))
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
