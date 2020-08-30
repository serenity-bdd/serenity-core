package net.serenitybdd.screenplay.questions;

import io.vavr.collection.List;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import java.util.Optional;

public class Attribute extends TargetedUIState<String> {

    private String attributeName;

    public Attribute(Target target, Actor actor, String attributeName) {
        super(target, actor);
        this.attributeName = attributeName;
    }

    public static TargetNamedAttributeBuilder of(Target target) {
        return new TargetNamedAttributeBuilder(target);
    }

    public static ByNamedAttributeBuilder of(By byLocator) {
        return new ByNamedAttributeBuilder(byLocator);
    }

    public static StringNamedAttributeBuilder of(String locator) {
        return new StringNamedAttributeBuilder(locator);
    }

    public static class TargetNamedAttributeBuilder {

        private final Target target;

        public TargetNamedAttributeBuilder(Target target) {
            this.target = target;
        }

        public UIStateReaderBuilder<Attribute> named(String name) {
            return new UIStateReaderBuilder<>(target, Attribute.class, Optional.of(name));
        }
    }

    public static class ByNamedAttributeBuilder {

        private final By byLocator;

        public ByNamedAttributeBuilder(By byLocator) {
            this.byLocator = byLocator;
        }

        public UIStateReaderBuilder<Attribute> named(String name) {
            return new UIStateReaderBuilder<>(Target.the(byLocator.toString()).located(byLocator), Attribute.class, Optional.of(name));
        }
    }

    public static class StringNamedAttributeBuilder {

        private final String locator;

        public StringNamedAttributeBuilder(String locator) {
            this.locator = locator;
        }

        public UIStateReaderBuilder<Attribute> named(String name) {
            return new UIStateReaderBuilder<>(Target.the(locator).locatedBy(locator), Attribute.class, Optional.of(name));
        }
    }

    public String resolve() {
        return target.resolveFor(actor).getAttribute(attributeName);
    }

    public java.util.List<String> resolveAll() {
        List<WebElementFacade> resolvedElements = List.ofAll(target.resolveAllFor(actor));
        return resolvedElements.map(element -> element.getAttribute(attributeName)).asJava();
    }
}