package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIStateReaderBuilder<T> {
    private final Target target;
    private final Class<T> type;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UIStateReaderBuilder(Target target, Class<T> type) {
        this.target = target;
        this.type = type;
    }

    public T onTheScreenOf(Actor actor) {
        try {
            return (T) type.getConstructor(Target.class, Actor.class).newInstance(target, actor);
        } catch (Exception e) {
            logger.error("Failed to instantiate UIStateReader of type " + type, e);
            throw new IllegalStateException("Failed to instantiate UIStateReader of type " + type, e);
        }
    }
}
