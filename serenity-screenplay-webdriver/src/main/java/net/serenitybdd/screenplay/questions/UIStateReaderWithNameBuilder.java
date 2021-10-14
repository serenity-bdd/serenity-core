package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class UIStateReaderWithNameBuilder<T> {
    protected final Target target;
    protected final Class<T> type;

    public UIStateReaderWithNameBuilder(Target target, Class<T> type) {
        this.target = target;
        this.type = type;
    }

    public PrimedUIStateReaderWithNameBuilder<T> named(String name) {
        return new PrimedUIStateReaderWithNameBuilder(target, type, name);
    }

    public static class PrimedUIStateReaderWithNameBuilder<T> extends UIStateReaderWithNameBuilder<T> {

        private final String name;

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        public PrimedUIStateReaderWithNameBuilder(Target target, Class<T> type, String name) {
            super(target, type);
            this.name = name;
        }

        public <T extends TargetedUIState> T viewedBy(Actor actor) {
            try {
                return (T) type.getConstructor(Target.class, Actor.class, String.class).newInstance(target, actor, name);
            } catch (Exception e) {
                logger.error("Failed to instantiate UIStateReader of type " + type, e);
                throw new IllegalStateException("Failed to instantiate UIStateReader of type " + type, e);
            }
        }
    }
}
